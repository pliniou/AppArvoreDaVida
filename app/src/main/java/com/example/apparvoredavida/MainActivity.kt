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
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.apparvoredavida.ui.navigation.AppNavigation
import com.example.apparvoredavida.ui.theme.AppTheme
import com.example.apparvoredavida.viewmodel.PreferenciasViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val preferenciasViewModel: PreferenciasViewModel = hiltViewModel()
    val navController = rememberNavController()
    
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation(
                    navController = navController
                )
            }
        }
    }
}