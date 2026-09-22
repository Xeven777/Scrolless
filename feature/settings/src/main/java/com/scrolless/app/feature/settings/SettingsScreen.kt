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
package com.scrolless.app.feature.settings

import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.scrolless.app.core.model.ThemeMode
import com.scrolless.app.designsystem.component.ScrollessSlider
import com.scrolless.app.designsystem.component.SettingsDivider
import com.scrolless.app.designsystem.component.SettingsGroup
import com.scrolless.app.designsystem.component.SettingsSectionLabel
import com.scrolless.app.designsystem.component.SettingsSwitchRow
import com.scrolless.app.designsystem.component.SettingsValuePill
import com.scrolless.app.designsystem.icon.ScrollessIcons
import com.scrolless.app.designsystem.layout.AdaptiveCenterBox
import com.scrolless.app.designsystem.layout.ContentMaxWidth
import com.scrolless.app.designsystem.theme.LocalSharedTransitionScope
import com.scrolless.app.designsystem.theme.SETTINGS_TRANSITION_KEY
import com.scrolless.app.designsystem.theme.ScrollessTheme
import com.scrolless.app.designsystem.theme.spacing
import com.scrolless.app.designsystem.util.rememberHapticHelper
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onPauseDurationChange = viewModel::onPauseDurationChange,
        onAllowVideosSentByDmChange = viewModel::onAllowVideosSentByDmChange,
        onTimerOverlayEnabledChange = viewModel::onTimerOverlayEnabledChange,
        onIncludeStoriesChange = viewModel::onIncludeStoriesChange,
        onThemeModeChange = viewModel::onThemeModeChange,
        onDynamicColorEnabledChange = viewModel::onDynamicColorEnabledChange,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onPauseDurationChange: (Int) -> Unit,
    onAllowVideosSentByDmChange: (Boolean) -> Unit,
    onTimerOverlayEnabledChange: (Boolean) -> Unit,
    onIncludeStoriesChange: (Boolean) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicColorEnabledChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current

    val hapticHelper = rememberHapticHelper()

    val sharedBoundsModifier = if (sharedTransitionScope != null) {
        val animatedVisibilityScope = LocalNavAnimatedContentScope.current
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = SETTINGS_TRANSITION_KEY),
                animatedVisibilityScope = animatedVisibilityScope,
                clipInOverlayDuringTransition = OverlayClip(clipShape = RoundedCornerShape(0.dp)),
            )
        }
    } else {
        Modifier
    }

    Scaffold(
        modifier = modifier.then(sharedBoundsModifier),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            hapticHelper.playTick()
                            onNavigateBack()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Icon(
                            painter = painterResource(id = ScrollessIcons.ArrowBack),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        AdaptiveCenterBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = ContentMaxWidth)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.spacing.large + 4.dp)
                    .padding(top = MaterialTheme.spacing.extraLarge - 2.dp, bottom = MaterialTheme.spacing.extraLarge + 4.dp),
            ) {
                SettingsSectionLabel(stringResource(R.string.settings_section_appearance))

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small + 2.dp))

                SettingsGroup {
                    ThemeModeItem(
                        themeMode = uiState.themeMode,
                        onThemeModeChange = onThemeModeChange,
                    )

                    // Dynamic colour only exists on Android 12+, so the toggle is hidden
                    // rather than shown as a dead switch on older releases.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        SettingsDivider()

                        SettingsSwitchRow(
                            title = stringResource(R.string.settings_material_you_title),
                            description = stringResource(R.string.settings_material_you_description),
                            note = stringResource(R.string.settings_material_you_note),
                            checked = uiState.dynamicColorEnabled,
                            onCheckedChange = onDynamicColorEnabledChange,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

                SettingsSectionLabel(stringResource(R.string.settings_section_blocking))

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small + 2.dp))

                SettingsGroup {
                    PauseDurationItem(
                        pauseDurationMinutes = uiState.pauseDurationMinutes,
                        onPauseDurationChange = onPauseDurationChange,
                    )

                    SettingsDivider()

                    AllowVideosSentByDmItem(
                        checked = uiState.allowVideosSentByDm,
                        onCheckedChange = onAllowVideosSentByDmChange,
                    )

                    SettingsDivider()

                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_include_stories_title),
                        description = stringResource(R.string.settings_include_stories_description),
                        note = stringResource(R.string.settings_include_stories_note),
                        checked = uiState.includeStories,
                        onCheckedChange = onIncludeStoriesChange,
                    )

                    SettingsDivider()

                    TimerOverlayItem(
                        checked = uiState.timerOverlayEnabled,
                        onCheckedChange = onTimerOverlayEnabledChange,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeItem(themeMode: ThemeMode, onThemeModeChange: (ThemeMode) -> Unit, modifier: Modifier = Modifier) {
    val hapticHelper = rememberHapticHelper()
    val options = remember {
        listOf(
            ThemeMode.SYSTEM to R.string.settings_theme_system,
            ThemeMode.LIGHT to R.string.settings_theme_light,
            ThemeMode.DARK to R.string.settings_theme_dark,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.large + 4.dp,
                vertical = MaterialTheme.spacing.large,
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small + 2.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_theme_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = stringResource(R.string.settings_theme_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.extraSmall),
        ) {
            options.forEachIndexed { index, (mode, labelRes) ->
                SegmentedButton(
                    selected = themeMode == mode,
                    onClick = {
                        if (themeMode != mode) {
                            hapticHelper.playConfirm()
                            onThemeModeChange(mode)
                        }
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    label = {
                        Text(
                            text = stringResource(labelRes),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun PauseDurationItem(pauseDurationMinutes: Int, onPauseDurationChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val hapticHelper = rememberHapticHelper()
    var sliderValue by remember(pauseDurationMinutes) {
        mutableIntStateOf(pauseDurationMinutes)
    }
    val valueLabel = pluralStringResource(R.plurals.settings_pause_duration_value, sliderValue, sliderValue)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.large + 4.dp, vertical = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small + 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        ) {
            Text(
                text = stringResource(R.string.settings_pause_duration_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            SettingsValuePill(
                text = valueLabel,
            )
        }
        Text(
            text = stringResource(R.string.settings_pause_duration_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ScrollessSlider(
            value = sliderValue.toFloat(),
            onValueChange = { sliderValue = it.roundToInt() },
            onValueChangeFinished = {
                if (sliderValue != pauseDurationMinutes) {
                    hapticHelper.playConfirm()
                    onPauseDurationChange(sliderValue)
                }
            },
            valueRange = 1f..15f,
            steps = 13,
            hapticTickOnDiscreteStep = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.extraSmall - 2.dp),
        )
    }
}

@Composable
private fun AllowVideosSentByDmItem(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    SettingsSwitchRow(
        title = stringResource(R.string.settings_allow_videos_sent_by_dms_title),
        description = stringResource(R.string.settings_allow_videos_sent_by_dms_description),
        note = stringResource(R.string.settings_allow_videos_sent_by_dms_note),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
    )
}

@Composable
private fun TimerOverlayItem(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    SettingsSwitchRow(
        title = stringResource(R.string.settings_show_onscreen_timer_title),
        description = stringResource(R.string.settings_show_onscreen_timer_description),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Preview(showBackground = true, device = Devices.PIXEL_7, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
    ScrollessTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(pauseDurationMinutes = 5, timerOverlayEnabled = true),
            onPauseDurationChange = {},
            onNavigateBack = {},
            onAllowVideosSentByDmChange = {},
            onTimerOverlayEnabledChange = {},
            onIncludeStoriesChange = {},
            onThemeModeChange = {},
            onDynamicColorEnabledChange = {},
        )
    }
}
