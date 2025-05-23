package com.example.apparvoredavida.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.apparvoredavida.ui.screens.*
import com.example.apparvoredavida.util.Constants

sealed class Screen(val route: String) {
    data object Home : Screen(Constants.ROUTE_HOME)
    data object Bible : Screen(Constants.ROUTE_BIBLE)
    data object Hymns : Screen(Constants.ROUTE_HYMNS)
    data object Scores : Screen(Constants.ROUTE_SCORES)
    data object Music : Screen(Constants.ROUTE_MUSIC)
    data object Settings : Screen(Constants.ROUTE_SETTINGS)
    object Player : Screen("${Constants.ROUTE_PLAYER}/{musicId}")
    object Viewer : Screen("${Constants.ROUTE_VIEWER}/{filePath}/{fileType}")
    data object Search : Screen(Constants.ROUTE_SEARCH)
    object Album : Screen("${Constants.ROUTE_ALBUM}/{albumId}")
    object AlbumDetail : Screen("album_detail/{albumId}") {
        fun createRoute(albumId: String) = "album_detail/$albumId"
    }

    companion object {
        val bottomNavItems = listOf(
            BottomNavItem(Home, "Início", Icons.Default.Home),
            BottomNavItem(Bible, "Bíblia", Icons.AutoMirrored.Filled.MenuBook),
            BottomNavItem(Hymns, "Hinário", Icons.Default.LibraryMusic),
            BottomNavItem(Music, "Músicas", Icons.Default.MusicNote),
        )
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                Screen.bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.screen.route,
                        onClick = {
                            navController.navigate(screen.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(
                route = Screen.Bible.route,
                arguments = listOf(
                    navArgument("book") { type = NavType.StringType; nullable = true },
                    navArgument("chapter") { type = NavType.IntType; nullable = true },
                    navArgument("verse") { type = NavType.IntType; nullable = true }
                )
            ) { backStackEntry ->
                val book = backStackEntry.arguments?.getString("book")
                val chapter = backStackEntry.arguments?.getInt("chapter")
                val verse = backStackEntry.arguments?.getInt("verse")
                BibleScreen(navController, book, chapter, verse)
            }
            composable(Screen.Hymns.route) {
                HinarioScreen(navController)
            }
            composable(Screen.Scores.route) {
                PartiturasScreen(navController)
            }
            composable(Screen.Music.route) {
                MusicasScreen(navController)
            }
            composable(Screen.Settings.route) {
                ConfiguracoesScreen(navController)
            }
            composable(Screen.Player.route) { backStackEntry ->
                val musicId = backStackEntry.arguments?.getString("musicId")
                if (musicId != null) {
                    ReprodutorScreen(navController, musicId)
                }
            }
            composable(Screen.Viewer.route) { backStackEntry ->
                val filePath = backStackEntry.arguments?.getString("filePath")
                val fileType = backStackEntry.arguments?.getString("fileType")
                if (filePath != null && fileType != null) {
                    VisualizadorScreen(navController, filePath, fileType)
                }
            }
            composable(Screen.Album.route) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString("albumId")
                if (albumId != null) {
                    AlbumScreen(navController, albumId)
                }
            }
            composable(
                route = Screen.AlbumDetail.route,
                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
            ) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString("albumId")
                if (albumId != null) {
                    AlbumDetailScreen(
                        navController = navController,
                        albumId = albumId
                    )
                } else {
                    // Lidar com erro de albumId nulo, talvez navegar de volta ou mostrar erro
                }
            }
        }
    }
}

// Função helper para criar a rota com versículo específico
fun Screen.Bible.createRouteWithVerse(book: String, chapter: Int, verse: Int): String {
    return "${Constants.ROUTE_BIBLE}?book=$book&chapter=$chapter&verse=$verse"
} 