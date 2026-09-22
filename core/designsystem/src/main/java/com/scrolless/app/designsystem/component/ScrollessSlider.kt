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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.scrolless.app.designsystem.util.rememberHapticHelper
import kotlin.math.roundToInt

/**
 * Branded slider: Scrolless track/tick colors.
 *
 * @param hapticTickOnDiscreteStep plays a tick whenever the rounded value
 * changes (for discrete sliders such as the pause-duration slider).
 */
@Composable
fun ScrollessSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    hapticTickOnDiscreteStep: Boolean = false,
) {
    val hapticHelper = rememberHapticHelper()
    var lastTickStep by remember { mutableIntStateOf(value.roundToInt()) }
    Slider(
        value = value,
        onValueChange = {
            if (hapticTickOnDiscreteStep) {
                val step = it.roundToInt()
                if (step != lastTickStep) {
                    hapticHelper.playTick()
                    lastTickStep = step
                }
            }
            onValueChange(it)
        },
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
            activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.56f),
            inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
        ),
    )
}
