package com.example.apparvoredavida.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.apparvoredavida.R
import java.io.IOException
import android.os.Build
import com.example.apparvoredavida.viewmodel.PreferenciasViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.apparvoredavida.util.TamanhoFonte
import com.example.apparvoredavida.model.TemaApp

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

val LocalAppTypography = staticCompositionLocalOf { Typography }

// Mapping from our string identifiers to R.font resources
private val fontResourceMap = mapOf(
    "poppins_regular.ttf" to R.font.poppins_regular,
    "RobotoSlab-Regular.ttf" to R.font.roboto_variablefont_wdth_wght, // Assuming this is the intended mapping
    "Lato-Regular.ttf" to R.font.outfit_variablefont_wght, // Assuming this is the intended mapping
    "exo2_variablefont_wght.ttf" to R.font.exo2_variablefont_wght,
    "opensans_variablefont_wdth_wght.ttf" to R.font.opensans_variablefont_wdth_wght,
    "outfit_variablefont_wght.ttf" to R.font.outfit_variablefont_wght,
    "poppins_black.ttf" to R.font.poppins_black,
    "poppins_bold.ttf" to R.font.poppins_bold,
    "poppins_extrabold.ttf" to R.font.poppins_extrabold,
    "poppins_extralight.ttf" to R.font.poppins_extralight,
    "poppins_light.ttf" to R.font.poppins_light,
    "poppins_medium.ttf" to R.font.poppins_medium,
    "poppins_semibold.ttf" to R.font.poppins_semibold,
    "poppins_thin.ttf" to R.font.poppins_thin,
    "roboto_variablefont_wdth_wght.ttf" to R.font.roboto_variablefont_wdth_wght,
    "rubik_variablefont_wght.ttf" to R.font.rubik_variablefont_wght,
    "sharetech_regular.ttf" to R.font.sharetech_regular
)

fun fontFamilyWithFallback(context: Context, fontFileName: String): FontFamily? {
    val fontResourceId = fontResourceMap[fontFileName.lowercase()] // Use lowercase for matching
    
    if (fontResourceId != null) {
        try {
            return FontFamily(Font(fontResourceId))
        } catch (e: Exception) {
            // Log the error or handle it appropriately
        }
    }

    // Fallback logic using resource IDs
    val fallbackFontResourceIds = listOf(
        R.font.poppins_regular, // Fallback 1
        R.font.roboto_variablefont_wdth_wght, // Fallback 2
        R.font.outfit_variablefont_wght // Fallback 3
    )

    for (fallbackId in fallbackFontResourceIds) {
        try {
            return FontFamily(Font(fallbackId))
        } catch (e: Exception) {
            // Log the error for fallback fonts
        }
    }

    return null // Uses default system font if all else fails
}

@Composable
fun AppTheme(
    viewModel: PreferenciasViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val preferenciasState = viewModel.preferencias.collectAsState()
    val preferencias = preferenciasState.value
    val systemDarkTheme = isSystemInDarkTheme()

    val darkTheme = remember(preferencias.tema, systemDarkTheme) {
        when (preferencias.tema) {
            TemaApp.SISTEMA -> systemDarkTheme
            TemaApp.CLARO -> false
            TemaApp.ESCURO -> true
        }
    }

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val fontFamily: FontFamily? = fontFamilyWithFallback(context, preferencias.fonte)
    val baseTypography = when (preferencias.tamanhoFonte) {
        TamanhoFonte.PEQUENO -> Typography.copy(
            bodyLarge = Typography.bodyLarge.copy(fontSize = 14.sp),
            titleLarge = Typography.titleLarge.copy(fontSize = 18.sp),
            headlineSmall = Typography.headlineSmall.copy(fontSize = 20.sp),
            titleMedium = Typography.titleMedium.copy(fontSize = 16.sp),
            bodySmall = Typography.bodySmall.copy(fontSize = 10.sp)
        )
        TamanhoFonte.MEDIO -> Typography
        TamanhoFonte.GRANDE -> Typography.copy(
            bodyLarge = Typography.bodyLarge.copy(fontSize = 20.sp),
            titleLarge = Typography.titleLarge.copy(fontSize = 26.sp),
            headlineSmall = Typography.headlineSmall.copy(fontSize = 32.sp),
            titleMedium = Typography.titleMedium.copy(fontSize = 22.sp),
            bodySmall = Typography.bodySmall.copy(fontSize = 14.sp)
        )
    }
    val customTypography = if (fontFamily != null) {
        baseTypography.copy(
            bodyLarge = baseTypography.bodyLarge.copy(fontFamily = fontFamily),
            titleLarge = baseTypography.titleLarge.copy(fontFamily = fontFamily),
            headlineSmall = baseTypography.headlineSmall.copy(fontFamily = fontFamily),
            titleMedium = baseTypography.titleMedium.copy(fontFamily = fontFamily),
            bodySmall = baseTypography.bodySmall.copy(fontFamily = fontFamily)
        )
    } else baseTypography

    CompositionLocalProvider(LocalAppTypography provides customTypography) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = customTypography,
            content = content
        )
    }
} 