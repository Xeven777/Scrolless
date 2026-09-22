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
package com.scrolless.app.designsystem.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Max content width for large screens (tablets / foldables open).
 *
 * Screens centre their scrolling column inside this cap so text and controls
 * don't stretch edge-to-edge on wide windows. 840dp follows the MD3
 * extra-large breakpoint guidance for readable single-column layouts.
 */
val ContentMaxWidth: Dp = 840.dp

/**
 * Centres [content] horizontally while capping its width at [ContentMaxWidth].
 *
 * Use inside Scaffold content or full-screen Boxes:
 * ```
 * AdaptiveCenterColumn {
 *     Column(Modifier.widthIn(max = ContentMaxWidth)...) { ... }
 * }
 * ```
 */
@Composable
fun AdaptiveCenterBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
    )
}
