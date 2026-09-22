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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun rememberPauseRemainingTime(pauseUntilMillis: Long): Long {
    val isInspectionMode = LocalInspectionMode.current

    fun calculateRemaining(): Long {
        if (pauseUntilMillis <= 0L) return 0L
        val delta = pauseUntilMillis - System.currentTimeMillis()
        return delta.coerceAtLeast(0L)
    }

    var remaining by remember(pauseUntilMillis) {
        mutableLongStateOf(calculateRemaining())
    }

    LaunchedEffect(pauseUntilMillis, isInspectionMode) {
        if (pauseUntilMillis <= 0L || isInspectionMode) {
            remaining = calculateRemaining()
        } else {
            while (isActive) {
                remaining = calculateRemaining()
                if (remaining <= 0L) break
                delay(1_000L.milliseconds)
            }
        }
    }

    return remaining
}
