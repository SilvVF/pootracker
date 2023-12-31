package io.silv.pootracker.presentation.util

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.silv.pootracker.R

val md_theme_light_primary = Color(0xFFA93728)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDAD4)
val md_theme_light_onPrimaryContainer = Color(0xFF410100)
val md_theme_light_secondary = Color(0xFF944A01)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDCC6)
val md_theme_light_onSecondaryContainer = Color(0xFF301400)
val md_theme_light_tertiary = Color(0xFF6750A4)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFE9DDFF)
val md_theme_light_onTertiaryContainer = Color(0xFF22005C)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF201A19)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF201A19)
val md_theme_light_surfaceVariant = Color(0xFFF5DDD9)
val md_theme_light_onSurfaceVariant = Color(0xFF534341)
val md_theme_light_outline = Color(0xFF857370)
val md_theme_light_inverseOnSurface = Color(0xFFFBEEEB)
val md_theme_light_inverseSurface = Color(0xFF362F2E)
val md_theme_light_inversePrimary = Color(0xFFFFB4A7)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFFA93728)
val md_theme_light_outlineVariant = Color(0xFFD8C2BE)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFFFB4A7)
val md_theme_dark_onPrimary = Color(0xFF670401)
val md_theme_dark_primaryContainer = Color(0xFF881F13)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDAD4)
val md_theme_dark_secondary = Color(0xFFFFB784)
val md_theme_dark_onSecondary = Color(0xFF4F2500)
val md_theme_dark_secondaryContainer = Color(0xFF713700)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDCC6)
val md_theme_dark_tertiary = Color(0xFFCFBCFF)
val md_theme_dark_onTertiary = Color(0xFF381E72)
val md_theme_dark_tertiaryContainer = Color(0xFF4F378A)
val md_theme_dark_onTertiaryContainer = Color(0xFFE9DDFF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF191925) // Color(0xFF27181B)
val md_theme_dark_onBackground = Color(0xFFEDE0DD)
val md_theme_dark_surface = Color(0xFF201A19)
val md_theme_dark_onSurface = Color(0xFFEDE0DD)
val md_theme_dark_surfaceVariant = Color(0xFF534341)
val md_theme_dark_onSurfaceVariant = Color(0xFFD8C2BE)
val md_theme_dark_outline = Color(0xFFA08C89)
val md_theme_dark_inverseOnSurface = Color(0xFF201A19)
val md_theme_dark_inverseSurface = Color(0xFFEDE0DD)
val md_theme_dark_inversePrimary = Color(0xFFA93728)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFFFB4A7)
val md_theme_dark_outlineVariant = Color(0xFF534341)
val md_theme_dark_scrim = Color(0xFF000000)

val seed = Color(0xFFB43F2F)

private val LightColors =
    lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )

private val DarkColors =
    darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )

private val defaultTypography = Typography()

private val montserrat =
    FontFamily(
        Font(R.font.montserrat_thin, FontWeight.Thin),
        Font(R.font.montserrat_black, FontWeight.Black),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
        Font(R.font.montserrat_medium, FontWeight.Medium),
        Font(R.font.montserrat_semibold, FontWeight.SemiBold),
        Font(R.font.montserrat_regular, FontWeight.Normal),
    )

private val appTypography =
    Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = montserrat),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = montserrat),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = montserrat),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = montserrat),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = montserrat),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = montserrat),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = montserrat),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = montserrat),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = montserrat),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = montserrat),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = montserrat),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = montserrat),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = montserrat),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = montserrat),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = montserrat),
    )

val Typography = appTypography

@Composable
fun PooTrackerTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit,
) {
    val colors =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (useDarkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            }

            useDarkTheme -> DarkColors
            else -> LightColors
        }
    CompositionLocalProvider {
        MaterialTheme(
            colorScheme = colors,
            content = content,
            typography = Typography,
        )
    }
}
