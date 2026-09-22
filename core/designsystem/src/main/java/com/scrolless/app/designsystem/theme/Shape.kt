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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * The MD3 shape scale, in ascending corner radius.
 *
 * Keep this a *scale*: every token must be rounder than the one before it, otherwise
 * components that pick neighbouring tokens end up looking unrelated. Previously
 * `small` was `RoundedCornerShape(percent = 50)` — a pill, i.e. rounder than
 * `extraLarge` — which is why chips, nav surfaces and cards never looked like they
 * belonged to one system.
 *
 * Full curves (pills, circles) are a deliberate per-component choice, not a scale
 * step: reach for [androidx.compose.foundation.shape.CircleShape] or
 * [ScrollessPillShape] at the call site instead.
 */
val ScrollessShapes = Shapes(
    extraSmall = RoundedCornerShape(size = 4.dp),
    small = RoundedCornerShape(size = 8.dp),
    medium = RoundedCornerShape(size = 12.dp),
    large = RoundedCornerShape(size = 16.dp),
    extraLarge = RoundedCornerShape(size = 28.dp),
)

/** Fully rounded "full" corner from the MD3 scale, for chips, pills and badges. */
val ScrollessPillShape = RoundedCornerShape(percent = 50)
