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
package com.scrolless.app.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * How today's usage compares to the goal the user set.
 *
 * This is the *only* place the app decides what a usage number means. Screens should
 * ask for a [UsageStatus] and read [UsageStatus.indicatorColor] rather than comparing
 * raw percentages themselves, so the ring, the limit chip, the background accent and
 * the weekly bars can never disagree about what counts as "a lot".
 *
 * Kept deliberately separate from the per-app brand palette in `Color.kt`
 * (`tiktokColor`, `reelsColor`, …). Brand colors answer "which app?"; status colors
 * answer "how am I doing?". Mixing the two in one visual layer — a ring whose arcs are
 * brand-colored but whose intensity is status-colored — leaves the user unable to tell
 * which question any given color is answering.
 */
enum class UsageStatus {
    /** Comfortably inside the goal, or no goal set and low usage. */
    COMFORTABLE,

    /** Past half the goal. Nudge, don't alarm. */
    APPROACHING,

    /** Goal reached. Blocking is now doing its job. */
    EXCEEDED,
}

/** Status color. Pair with [indicatorContainerColor] for a tonal fill behind it. */
val UsageStatus.indicatorColor: Color
    get() = when (this) {
        UsageStatus.COMFORTABLE -> progressbar_green_use
        UsageStatus.APPROACHING -> progressbar_orange_use
        UsageStatus.EXCEEDED -> progressbar_red_use
    }

/** Low-emphasis version of [indicatorColor], for fills behind text or bars. */
val UsageStatus.indicatorContainerColor: Color
    get() = indicatorColor.copy(alpha = 0.16f)

/**
 * Thresholds as fractions of the goal. Tuned so that "approaching" begins at the
 * halfway mark and "exceeded" aligns with blocking actually kicking in.
 */
private const val APPROACHING_THRESHOLD = 0.5f
private const val EXCEEDED_THRESHOLD = 1.0f

/**
 * Classifies a 0–100 progress percentage, as reported by the blocking layer.
 *
 * @param progressPercent percent of the goal consumed; values outside 0–100 are clamped.
 * @param hasGoal when false the user has no limit configured, so usage carries no
 *   status and the result is always [UsageStatus.COMFORTABLE].
 */
fun usageStatusFor(progressPercent: Int, hasGoal: Boolean = true): UsageStatus {
    if (!hasGoal) return UsageStatus.COMFORTABLE
    val fraction = (progressPercent.coerceIn(0, 100)) / 100f
    return statusForFraction(fraction)
}

/**
 * Classifies raw usage against a goal in milliseconds.
 *
 * Prefer this overload when a goal exists: it stays correct for elapsed usage that has
 * not yet been reported as a percentage (for example a historical day in the weekly
 * bars).
 */
fun usageStatusFor(usedMillis: Long, limitMillis: Long): UsageStatus {
    if (limitMillis <= 0L) return UsageStatus.COMFORTABLE
    val used = usedMillis.coerceAtLeast(0L)
    return statusForFraction(used.toDouble().div(limitMillis.toDouble()).toFloat())
}

private fun statusForFraction(fraction: Float): UsageStatus = when {
    fraction >= EXCEEDED_THRESHOLD -> UsageStatus.EXCEEDED
    fraction >= APPROACHING_THRESHOLD -> UsageStatus.APPROACHING
    else -> UsageStatus.COMFORTABLE
}
