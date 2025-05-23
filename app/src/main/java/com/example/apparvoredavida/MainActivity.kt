package com.example.apparvoredavida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apparvoredavida.ui.navigation.AppNavigation
import com.example.apparvoredavida.ui.theme.AppTheme
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.viewmodel.PreferenciasViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.apparvoredavida.data.PreferenciasDataClass
import com.example.apparvoredavida.util.TamanhoFonte

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            val preferenciasViewModel: PreferenciasViewModel = hiltViewModel()
            
            val preferenciasState by preferenciasViewModel.preferencias.collectAsStateWithLifecycle()
            
            val useDarkTheme = when (preferenciasState.tema) {
                com.example.apparvoredavida.model.TemaApp.ESCURO -> true
                com.example.apparvoredavida.model.TemaApp.CLARO -> false
                com.example.apparvoredavida.model.TemaApp.SISTEMA -> isSystemInDarkTheme()
            }

            AppTheme(
                darkTheme = useDarkTheme,
                temaApp = preferenciasState.tema,
                fontePreferida = preferenciasState.fonte,
                tamanhoFontePreferido = preferenciasState.tamanhoFonte
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}