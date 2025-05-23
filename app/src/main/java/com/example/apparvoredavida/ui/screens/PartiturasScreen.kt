package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartiturasScreen(navController: NavController) {
    val context = LocalContext.current
    val pdfFiles = remember { AssetManager.listPdfFiles(context) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Partituras",
            onBackClick = { navController.navigateUp() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botão Hinos
            Button(
                onClick = {
                    val hinosPdf = pdfFiles.find { it.contains("partituras_hinos") }
                    if (hinosPdf != null) {
                        navController.navigate("${Constants.ROUTE_VIEWER}/$hinosPdf/pdf")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Hinos")
            }

            // Botão Cânticos
            Button(
                onClick = {
                    val canticosPdf = pdfFiles.find { it.contains("partituras_canticos") }
                    if (canticosPdf != null) {
                        navController.navigate("${Constants.ROUTE_VIEWER}/$canticosPdf/pdf")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cânticos")
            }

            // Botão Suplementos
            Button(
                onClick = {
                    val suplementosPdf = pdfFiles.find { it.contains("partituras_suplementos") }
                    if (suplementosPdf != null) {
                        navController.navigate("${Constants.ROUTE_VIEWER}/$suplementosPdf/pdf")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Suplementos")
            }
        }
    }
} 