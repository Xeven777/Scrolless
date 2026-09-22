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

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.scrolless.app.core.designsystem.R

/**
 * Google Sans Flex — Google's brand typeface, distributed under the SIL Open Font
 * License v1.1 (see `ASSETS_LICENSE`).
 *
 * One 215 KB variable font whose `wght` axis spans 400–700 supplies every weight.
 *
 * Each entry pins the axis explicitly via [FontVariation.Settings] rather than relying on
 * the platform to infer weight from the requested [FontWeight]. Without that, the family
 * resolves to the font's default instance and every weight renders at Regular, which
 * silently flattens the whole type hierarchy the scale in `Type.kt` sets up.
 *
 * Variable fonts require API 26+, which is this module's `minSdk`, so no compatibility
 * branch is needed.
 *
 * Glyph coverage is Latin, Latin Extended-A/B and Vietnamese. Scripts outside that range
 * (Cyrillic, Greek, Devanagari, CJK, …) fall back to the system font.
 */
val GoogleSansFlex = FontFamily(
    googleSansFlexFont(FontWeight.Normal),
    googleSansFlexFont(FontWeight.Medium),
    googleSansFlexFont(FontWeight.SemiBold),
    googleSansFlexFont(FontWeight.Bold),
)

private fun googleSansFlexFont(weight: FontWeight): Font = Font(
    resId = R.font.google_sans_flex,
    weight = weight,
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)
