package com.example.apparvoredavida.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.apparvoredavida.model.Partitura

class PartiturasViewModel(application: Application) : AndroidViewModel(application) {
    private val _partituras = MutableStateFlow<List<Partitura>>(emptyList())
    val partituras: StateFlow<List<Partitura>> = _partituras

    init {
        carregarPartituras()
    }

    private fun carregarPartituras() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val partituras = mutableListOf<Partitura>()
            
            // Carregar partituras do arquivo JSON em assets/partituras/index.json
            try {
                val json = context.assets.open("partituras/index.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Partitura>>() {}.type
                val partiturasJson: List<Partitura> = Gson().fromJson(json, type)
                partituras.addAll(partiturasJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            _partituras.value = partituras
        }
    }

    fun getScoreDetails(scoreId: String): Partitura? {
        // Assuming scoreId is the 'id' of the Partitura as defined in the global model.
        val partitura = _partituras.value.find { it.id == scoreId }
        return partitura
    }
} 