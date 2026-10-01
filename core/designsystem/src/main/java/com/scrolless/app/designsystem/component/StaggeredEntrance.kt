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
package com.scrolless.app.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val STAGGER_MILLIS = 70L
private val ENTRANCE_OFFSET = 16.dp

/**
 * One-shot entrance polish: content fades in and drifts up into place on first composition.
 *
 * Pass increasing [index] values down a column to stagger siblings; a single block uses `0`.
 * Only `alpha` and `translationY` are animated, so nothing reflows and the scroll position is
 * never disturbed. The lift uses a slightly bouncy spring so arriving content has a touch of
 * weight instead of the flat feel of a linear fade.
 *
 * Skipped in previews, where content is shown in its final state.
 */
@Composable
fun Modifier.staggeredEntrance(index: Int = 0): Modifier {
    val isPreview = LocalInspectionMode.current
    val progress = remember { Animatable(if (isPreview) 1f else 0f) }
    val offsetPx = with(LocalDensity.current) { ENTRANCE_OFFSET.toPx() }

    LaunchedEffect(Unit) {
        if (isPreview) return@LaunchedEffect
        if (index > 0) delay(index * STAGGER_MILLIS)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        )
    }

    return graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * offsetPx
    }
}
