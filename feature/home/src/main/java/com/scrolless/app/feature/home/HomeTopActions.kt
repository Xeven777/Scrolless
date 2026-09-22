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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.scrolless.app.designsystem.icon.ScrollessIcons
import com.scrolless.app.designsystem.theme.LocalSharedTransitionScope
import com.scrolless.app.designsystem.theme.SETTINGS_TRANSITION_KEY
import com.scrolless.app.designsystem.util.rememberHapticHelper

@Composable
fun HelpButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val hapticHelper = rememberHapticHelper()
    FilledTonalIconButton(
        onClick = {
            hapticHelper.playClick()
            onClick()
        },
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = ScrollessIcons.Help),
            contentDescription = stringResource(R.string.help),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SettingsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val hapticHelper = rememberHapticHelper()
    val sharedTransitionScope = LocalSharedTransitionScope.current

    val sharedBoundsModifier = if (sharedTransitionScope != null) {
        val animatedVisibilityScope = LocalNavAnimatedContentScope.current
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = SETTINGS_TRANSITION_KEY),
                animatedVisibilityScope = animatedVisibilityScope,
                clipInOverlayDuringTransition = OverlayClip(clipShape = CircleShape),
            )
        }
    } else {
        Modifier
    }

    FilledTonalIconButton(
        onClick = {
            hapticHelper.playClick()
            onClick()
        },
        modifier = modifier.then(sharedBoundsModifier),
    ) {
        Icon(
            painter = painterResource(id = ScrollessIcons.Settings),
            contentDescription = stringResource(R.string.settings),
        )
    }
}
