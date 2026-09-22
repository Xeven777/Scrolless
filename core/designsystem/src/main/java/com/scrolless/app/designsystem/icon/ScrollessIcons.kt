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
package com.scrolless.app.designsystem.icon

import androidx.annotation.DrawableRes
import com.scrolless.app.core.designsystem.R

/**
 * The single source of truth for the app's icon set.
 *
 * Every entry is a 24dp [Material Symbols](https://fonts.google.com/icons) vector living in this
 * module, so all screens share one family, one stroke weight and one optical size. Feature modules
 * reach the drawables through this object rather than importing this module's `R` class, which
 * would collide with their own.
 *
 * Icons are untinted 24dp vectors: pass them to a tinting composable (`Icon`, `AnimatedIcon`,
 * `PopupCircleIcon`) and the content colour is applied for you.
 *
 * Two deliberate exceptions to the outlined style, both rendered large enough that a hairline
 * outline reads as weak: [CheckCircle] on the confirmation sheet, and [Pause] / [PlayArrow] for the
 * pause/resume control.
 */
object ScrollessIcons {

    @DrawableRes
    val ArrowBack = R.drawable.ic_arrow_back

    @DrawableRes
    val ArrowDropDown = R.drawable.ic_arrow_drop_down

    @DrawableRes
    val Block = R.drawable.ic_block

    @DrawableRes
    val CheckCircle = R.drawable.ic_check_circle

    @DrawableRes
    val ChevronLeft = R.drawable.ic_chevron_left

    @DrawableRes
    val ChevronRight = R.drawable.ic_chevron_right

    @DrawableRes
    val Close = R.drawable.ic_close

    @DrawableRes
    val Help = R.drawable.ic_help

    @DrawableRes
    val Info = R.drawable.ic_info

    @DrawableRes
    val Pause = R.drawable.ic_pause

    @DrawableRes
    val PlayArrow = R.drawable.ic_play_arrow

    @DrawableRes
    val Schedule = R.drawable.ic_schedule

    @DrawableRes
    val Settings = R.drawable.ic_settings

    @DrawableRes
    val Timer = R.drawable.ic_timer

    @DrawableRes
    val Tune = R.drawable.ic_tune
}
