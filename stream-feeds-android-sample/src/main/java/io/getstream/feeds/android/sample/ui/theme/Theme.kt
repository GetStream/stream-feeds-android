/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.feeds.android.sample.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme()
private val LightColorScheme = lightColorScheme()

private val LegacyDarkColors =
    androidx.compose.material.darkColors(
        primary = DarkColorScheme.primary,
        primaryVariant = DarkColorScheme.primaryContainer,
        secondary = DarkColorScheme.secondary,
        background = DarkColorScheme.background,
        surface = DarkColorScheme.surface,
        onPrimary = DarkColorScheme.onPrimary,
        onSecondary = DarkColorScheme.onSecondary,
        onBackground = DarkColorScheme.onBackground,
        onSurface = DarkColorScheme.onSurface,
    )
private val LegacyLightColors =
    androidx.compose.material.lightColors(
        primary = LightColorScheme.primary,
        primaryVariant = LightColorScheme.primaryContainer,
        secondary = LightColorScheme.secondary,
        background = LightColorScheme.background,
        surface = LightColorScheme.surface,
        onPrimary = LightColorScheme.onPrimary,
        onSecondary = LightColorScheme.onSecondary,
        onBackground = LightColorScheme.onBackground,
        onSurface = LightColorScheme.onSurface,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    val legacyColors =
        when (darkTheme) {
            true -> LegacyDarkColors
            false -> LegacyLightColors
        }

    // Also apply Material 2 theme because the bottom sheet from Compose Destinations uses it. We
    // can remove this alongside the material 2 library once they add support
    // https://github.com/raamcosta/compose-destinations/issues/756.
    androidx.compose.material.MaterialTheme(colors = legacyColors) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
