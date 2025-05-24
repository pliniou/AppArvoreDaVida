package com.example.apparvoredavida.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.apparvoredavida.data.PreferenciasDataClass
import com.example.apparvoredavida.model.TemaApp
import com.example.apparvoredavida.model.TamanhoFonte
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.apparvoredavida.data.repository.PreferencesRepository

/**
 * ViewModel responsável por gerenciar as preferências do usuário.
 * Implementa configurações de tema, tamanho de fonte e outras preferências.
 */
@HiltViewModel
class PreferenciasViewModel @Inject constructor(
    application: Application,
    private val dataStore: DataStore<Preferences>,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private object PreferencesKeys {
        val TEMA_APP = stringPreferencesKey("tema_app")
        val FONTE_KEY = stringPreferencesKey("fonte")
        val TAMANHO_FONTE_KEY = intPreferencesKey("tamanho_fonte")
    }

    // Mudando para MutableStateFlow interno
    private val _preferencias = MutableStateFlow(PreferenciasDataClass())
    val preferencias: StateFlow<PreferenciasDataClass> = _preferencias.asStateFlow()

    // Adicionando StateFlow para fontes disponíveis
    private val _fontesDisponiveis = MutableStateFlow<List<String>>(emptyList())
    val fontesDisponiveis: StateFlow<List<String>> = _fontesDisponiveis.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _fontSize = MutableStateFlow(TamanhoFonte.MEDIO)
    val fontSize: StateFlow<TamanhoFonte> = _fontSize.asStateFlow()

    init {
        restaurarPreferencias()
        listarFontes()
        viewModelScope.launch {
            loadPreferences()
        }
    }

    // Função para salvar todas as preferências
    private fun salvarPreferencias(preferenciasSalvar: PreferenciasDataClass) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.TEMA_APP] = preferenciasSalvar.tema.name
                prefs[PreferencesKeys.FONTE_KEY] = preferenciasSalvar.fonte
                prefs[PreferencesKeys.TAMANHO_FONTE_KEY] = preferenciasSalvar.tamanhoFonte.ordinal
                Log.d("PreferenciasVM", "Preferências salvas: Tema=${preferenciasSalvar.tema.name}, Fonte=${preferenciasSalvar.fonte}, Tamanho=${preferenciasSalvar.tamanhoFonte.name}")
            }
        }
    }

    // Função para restaurar todas as preferências
    private fun restaurarPreferencias() {
        viewModelScope.launch {
            try {
                val prefs = dataStore.data.first()
                val tema = TemaApp.valueOf(prefs[PreferencesKeys.TEMA_APP] ?: TemaApp.SISTEMA.name)
                val fonte = prefs[PreferencesKeys.FONTE_KEY] ?: "default"
                val tamanho = TamanhoFonte.values().getOrElse(prefs[PreferencesKeys.TAMANHO_FONTE_KEY] ?: TamanhoFonte.MEDIO.ordinal) { TamanhoFonte.MEDIO }

                // Atualizar o MutableStateFlow
                _preferencias.value = PreferenciasDataClass(tema, fonte, tamanho)

                Log.d("PreferenciasVM", "Preferências restauradas: Tema=${tema.name}, Fonte=${fonte}, Tamanho=${tamanho.name}")

            } catch (e: Exception) {
                Log.e("PreferenciasVM", "Erro ao restaurar preferências: ${e.message}")
                // Em caso de erro, manter os valores iniciais do MutableStateFlow
                _preferencias.value = PreferenciasDataClass() // Ou definir valores default explícitos se diferentes do inicial
            }
        }
    }

    // Funções de atualização para a UI usar
    fun atualizarTema(tema: TemaApp) {
        val novasPreferencias = _preferencias.value.copy(tema = tema)
        _preferencias.value = novasPreferencias
        salvarPreferencias(novasPreferencias)
    }

    fun atualizarFonte(fonte: String) {
        val novasPreferencias = _preferencias.value.copy(fonte = fonte)
        _preferencias.value = novasPreferencias
        salvarPreferencias(novasPreferencias)
    }

    fun atualizarTamanhoFonte(tamanho: TamanhoFonte) {
        val novasPreferencias = _preferencias.value.copy(tamanhoFonte = tamanho)
        _preferencias.value = novasPreferencias
        salvarPreferencias(novasPreferencias)
    }

    // Função para listar fontes disponíveis em assets/fonts
    private fun listarFontes() {
        viewModelScope.launch {
            try {
                val fontes = context.assets.list("fonts")?.filter { it.endsWith(".ttf") } ?: emptyList()
                _fontesDisponiveis.value = listOf("default") + fontes
                 Log.d("PreferenciasVM", "Fontes disponíveis carregadas: ${_fontesDisponiveis.value.size} fontes.")
            } catch (e: Exception) {
                Log.e("PreferenciasVM", "Erro ao listar fontes: ${e.message}")
                _fontesDisponiveis.value = listOf("default") // Manter apenas a opção default em caso de erro
            }
        }
    }

    /**
     * Carrega as preferências do usuário do DataStore.
     */
    private suspend fun loadPreferences() {
        try {
            _isDarkMode.value = preferencesRepository.getDarkMode()
            _fontSize.value = preferencesRepository.getFontSize()
        } catch (e: Exception) {
            Log.e("PreferenciasVM", "Erro ao carregar preferências: ${e.message}")
        }
    }

    /**
     * Alterna o modo escuro.
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            try {
                val newValue = !_isDarkMode.value
                preferencesRepository.setDarkMode(newValue)
                _isDarkMode.value = newValue
            } catch (e: Exception) {
                Log.e("PreferenciasVM", "Erro ao alternar modo escuro: ${e.message}")
            }
        }
    }

    /**
     * Define o tamanho da fonte.
     * @param size Novo tamanho de fonte
     */
    fun setFontSize(size: TamanhoFonte) {
        viewModelScope.launch {
            try {
                preferencesRepository.setFontSize(size)
                _fontSize.value = size
            } catch (e: Exception) {
                Log.e("PreferenciasVM", "Erro ao definir tamanho da fonte: ${e.message}")
            }
        }
    }
} 