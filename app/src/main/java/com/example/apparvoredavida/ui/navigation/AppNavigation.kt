package com.example.apparvoredavida.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.apparvoredavida.ui.screens.*
import com.example.apparvoredavida.util.Constants

sealed class Screen(val route: String) {
    object Home : Screen(Constants.ROUTE_HOME)
    object Bible : Screen(Constants.ROUTE_BIBLE)
    object Hymns : Screen(Constants.ROUTE_HYMNS)
    object Scores : Screen(Constants.ROUTE_SCORES)
    object Music : Screen(Constants.ROUTE_MUSIC)
    object Favorites : Screen(Constants.ROUTE_FAVORITES)
    object Settings : Screen(Constants.ROUTE_SETTINGS)
    object Player : Screen("${Constants.ROUTE_PLAYER}/{musicId}")
    object Viewer : Screen("${Constants.ROUTE_VIEWER}/{fileId}/{fileType}")
    object Album : Screen("${Constants.ROUTE_ALBUM}/{albumId}")
    object AlbumDetail : Screen("album_detail/{albumId}")

    fun createRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    companion object {
        val bottomNavItems = listOf(
            BottomNavItem(Home, "Início", Icons.Default.Home),
            BottomNavItem(Bible, "Bíblia", Icons.AutoMirrored.Filled.MenuBook),
            BottomNavItem(Hymns, "Hinário", Icons.Default.LibraryMusic),
            BottomNavItem(Music, "Músicas", Icons.Default.MusicNote),
            BottomNavItem(Favorites, "Favoritos", Icons.Default.Favorite)
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
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Bible.route) {
            BibleScreen(navController)
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

        composable(Screen.Favorites.route) {
            FavoritesScreen(navController)
        }

        composable(Screen.Settings.route) {
            ConfiguracoesScreen(navController)
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("musicId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ReprodutorScreen(
                navController = navController,
                musicId = backStackEntry.arguments?.getString("musicId") ?: ""
            )
        }

        composable(
            route = Screen.Viewer.route,
            arguments = listOf(
                navArgument("fileId") { type = NavType.StringType },
                navArgument("fileType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            VisualizadorScreen(
                navController = navController,
                fileId = backStackEntry.arguments?.getString("fileId") ?: "",
                fileType = backStackEntry.arguments?.getString("fileType") ?: ""
            )
        }

        composable(
            route = Screen.Album.route,
            arguments = listOf(
                navArgument("albumId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AlbumScreen(
                navController = navController,
                albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            )
        }

        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(
                navArgument("albumId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AlbumDetailScreen(
                navController = navController,
                albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            )
        }
    }
}

// Função helper para criar a rota com versículo específico
fun Screen.Bible.createRouteWithVerse(book: String, chapter: Int, verse: Int): String {
    return "${Constants.ROUTE_BIBLE}?book=$book&chapter=$chapter&verse=$verse"
} 