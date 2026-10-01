/*
 * Copyright (C) 2026 Scrolless
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.scrolless.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.scrolless.app.designsystem.util.radialGradientScrim

@Composable
internal fun HomeBackground(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    accentStrength: Float = 0f,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(
                    baseColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f),
                    accentColor = accentColor?.copy(alpha = 0.13f),
                    accentStrength = accentStrength,
                ),
        )
        content()
    }
}
