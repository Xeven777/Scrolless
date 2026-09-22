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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.scrolless.app.core.model.BlockOption
import com.scrolless.app.core.model.SessionSegment
import com.scrolless.app.designsystem.layout.ContentMaxWidth
import com.scrolless.app.designsystem.theme.spacing
import com.scrolless.app.feature.home.components.InlineUsageAnalyticsPanel
import com.scrolless.app.feature.home.components.TodayBlockingControls
import com.scrolless.app.feature.home.components.WeekdayAverageSection
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onBlockOptionSelected: (BlockOption) -> Unit,
    onConfigureDailyLimit: () -> Unit,
    onHelpClicked: () -> Unit,
    onIntervalTimerClick: () -> Unit,
    onIntervalTimerEdit: () -> Unit,
    onPauseToggle: (Boolean) -> Unit,
    onDebugUsageChanged: (List<SessionSegment>) -> Unit = {},
    onDebugUsageReset: () -> Unit = {},
    onUsageAnalyticsDateSelected: (LocalDate) -> Unit = {},
    onUsageAnalyticsTodaySelected: () -> Unit = {},
    onAveragePeriodSelected: (UsageAveragePeriod) -> Unit = {},
    forceLegacyOverlay: Boolean = false,
    onForceLegacyOverlayChanged: (Boolean) -> Unit = {},
) {
    val pauseRemainingMillis = rememberPauseRemainingTime(uiState.pauseUntilMillis)
    val isPauseActive = pauseRemainingMillis > 0L
    val showDebugPanel = BuildConfig.DEBUG || LocalInspectionMode.current
    var isDebugExpanded by remember { mutableStateOf(false) }
    var sessionChunksExpanded by remember(uiState.usageAnalytics.selectedDate) { mutableStateOf(false) }

    val analytics = uiState.usageAnalytics
    val todayPage = remember(analytics.dataStartDate, analytics.today) {
        ChronoUnit.DAYS.between(analytics.dataStartDate, analytics.today).toInt()
    }
    val selectedPage = remember(analytics.dataStartDate, analytics.selectedDate, todayPage) {
        ChronoUnit.DAYS.between(analytics.dataStartDate, analytics.selectedDate).toInt()
            .coerceIn(0, todayPage.coerceAtLeast(0))
    }
    val pagerState = rememberPagerState(
        initialPage = selectedPage,
        pageCount = { (todayPage + 1).coerceAtLeast(1) },
    )

    val dateSwipeThresholdPx = with(LocalDensity.current) { 32.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()

    val isBlockingActive = resolveIsBlockingActive(uiState)
    val blockingState = resolveHomeBlockingState(
        blockOption = uiState.blockOption,
        isPauseActive = isPauseActive,
        isBlockingActive = isBlockingActive,
        hasConfiguredGoal = uiState.hasConfiguredGoal,
    )

    var isInitialPageLoad by remember { mutableStateOf(true) }

    LaunchedEffect(selectedPage) {
        if (pagerState.currentPage != selectedPage) {
            if (isInitialPageLoad) {
                isInitialPageLoad = false
                pagerState.scrollToPage(selectedPage)
            } else {
                pagerState.animateScrollToPage(selectedPage)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .dateSwipeGesture(
                pagerState = pagerState,
                todayPage = todayPage,
                dateSwipeThresholdPx = dateSwipeThresholdPx,
                analytics = analytics,
                selectedPage = selectedPage,
                coroutineScope = coroutineScope,
                onUsageAnalyticsDateSelected = onUsageAnalyticsDateSelected,
            ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .widthIn(max = ContentMaxWidth)
                .padding(horizontal = MaterialTheme.spacing.large),
        ) {
            val isWide = maxWidth >= 840.dp
            if (isWide) {
                HomeWidePanes(
                    uiState = uiState,
                    analyticsSelectedIsToday = analytics.selectedDate == analytics.today,
                    blockingState = blockingState,
                    pagerState = pagerState,
                    todayPage = todayPage,
                    selectedPage = selectedPage,
                    isBlockingActive = isBlockingActive,
                    isPauseActive = isPauseActive,
                    pauseRemainingMillis = pauseRemainingMillis,
                    sessionChunksExpanded = sessionChunksExpanded,
                    onToggleSessionChunks = { sessionChunksExpanded = !sessionChunksExpanded },
                    onNavigateToSettings = onNavigateToSettings,
                    onBlockOptionSelected = onBlockOptionSelected,
                    onConfigureDailyLimit = onConfigureDailyLimit,
                    onHelpClicked = onHelpClicked,
                    onIntervalTimerClick = onIntervalTimerClick,
                    onIntervalTimerEdit = onIntervalTimerEdit,
                    onPauseToggle = onPauseToggle,
                    onUsageAnalyticsDateSelected = onUsageAnalyticsDateSelected,
                    onUsageAnalyticsTodaySelected = onUsageAnalyticsTodaySelected,
                    onAveragePeriodSelected = onAveragePeriodSelected,
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UsageOverviewHeader(
                        uiState = uiState,
                        analytics = analytics,
                        blockingState = blockingState,
                        pauseRemainingMillis = pauseRemainingMillis,
                        pagerState = pagerState,
                        todayPage = todayPage,
                        selectedPage = selectedPage,
                        onUsageAnalyticsDateSelected = onUsageAnalyticsDateSelected,
                        onUsageAnalyticsTodaySelected = onUsageAnalyticsTodaySelected,
                        onHelpClicked = onHelpClicked,
                        onNavigateToSettings = onNavigateToSettings,
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        HomeControlsAndAnalytics(
                            uiState = uiState,
                            analyticsSelectedIsToday = analytics.selectedDate == analytics.today,
                            isBlockingActive = isBlockingActive,
                            isPauseActive = isPauseActive,
                            pauseRemainingMillis = pauseRemainingMillis,
                            sessionChunksExpanded = sessionChunksExpanded,
                            onToggleSessionChunks = { sessionChunksExpanded = !sessionChunksExpanded },
                            onBlockOptionSelected = onBlockOptionSelected,
                            onConfigureDailyLimit = onConfigureDailyLimit,
                            onIntervalTimerClick = onIntervalTimerClick,
                            onIntervalTimerEdit = onIntervalTimerEdit,
                            onPauseToggle = onPauseToggle,
                            onAveragePeriodSelected = onAveragePeriodSelected,
                        )
                    }
                }
            }
        }

        if (showDebugPanel) {
            com.scrolless.app.feature.home.debug.FloatingDebugUsagePanel(
                sessionSegments = analytics.sessionSegments,
                selectedDate = analytics.selectedDate,
                isExpanded = isDebugExpanded,
                onToggleExpanded = { isDebugExpanded = !isDebugExpanded },
                onUsageChanged = onDebugUsageChanged,
                onReset = {
                    onDebugUsageReset()
                },
                forceLegacyOverlay = forceLegacyOverlay,
                onForceLegacyOverlayChanged = onForceLegacyOverlayChanged,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun HomeWidePanes(
    uiState: HomeUiState,
    analyticsSelectedIsToday: Boolean,
    blockingState: HomeBlockingState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    todayPage: Int,
    selectedPage: Int,
    isBlockingActive: Boolean,
    isPauseActive: Boolean,
    pauseRemainingMillis: Long,
    sessionChunksExpanded: Boolean,
    onToggleSessionChunks: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onBlockOptionSelected: (BlockOption) -> Unit,
    onConfigureDailyLimit: () -> Unit,
    onHelpClicked: () -> Unit,
    onIntervalTimerClick: () -> Unit,
    onIntervalTimerEdit: () -> Unit,
    onPauseToggle: (Boolean) -> Unit,
    onUsageAnalyticsDateSelected: (LocalDate) -> Unit,
    onUsageAnalyticsTodaySelected: () -> Unit,
    onAveragePeriodSelected: (UsageAveragePeriod) -> Unit,
) {
    val analytics = uiState.usageAnalytics
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UsageOverviewHeader(
                uiState = uiState,
                analytics = analytics,
                blockingState = blockingState,
                pauseRemainingMillis = pauseRemainingMillis,
                pagerState = pagerState,
                todayPage = todayPage,
                selectedPage = selectedPage,
                onUsageAnalyticsDateSelected = onUsageAnalyticsDateSelected,
                onUsageAnalyticsTodaySelected = onUsageAnalyticsTodaySelected,
                onHelpClicked = onHelpClicked,
                onNavigateToSettings = onNavigateToSettings,
            )
            if (analyticsSelectedIsToday) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
                WeekdayAverageSection(
                    weekdayAverages = analytics.weekdayAverages,
                    selectedPeriod = uiState.averagePeriod,
                    referenceLimitMillis = uiState.weekdayReferenceMillis,
                    onPeriodSelected = { onAveragePeriodSelected(it) },
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeControlsAndAnalytics(
                uiState = uiState,
                analyticsSelectedIsToday = analyticsSelectedIsToday,
                isBlockingActive = isBlockingActive,
                isPauseActive = isPauseActive,
                pauseRemainingMillis = pauseRemainingMillis,
                sessionChunksExpanded = sessionChunksExpanded,
                onToggleSessionChunks = onToggleSessionChunks,
                onBlockOptionSelected = onBlockOptionSelected,
                onConfigureDailyLimit = onConfigureDailyLimit,
                onIntervalTimerClick = onIntervalTimerClick,
                onIntervalTimerEdit = onIntervalTimerEdit,
                onPauseToggle = onPauseToggle,
                onAveragePeriodSelected = onAveragePeriodSelected,
                showWeekdayAverages = false,
            )
        }
    }
}

@Composable
private fun HomeControlsAndAnalytics(
    uiState: HomeUiState,
    analyticsSelectedIsToday: Boolean,
    isBlockingActive: Boolean,
    isPauseActive: Boolean,
    pauseRemainingMillis: Long,
    sessionChunksExpanded: Boolean,
    onToggleSessionChunks: () -> Unit,
    onBlockOptionSelected: (BlockOption) -> Unit,
    onConfigureDailyLimit: () -> Unit,
    onIntervalTimerClick: () -> Unit,
    onIntervalTimerEdit: () -> Unit,
    onPauseToggle: (Boolean) -> Unit,
    onAveragePeriodSelected: (UsageAveragePeriod) -> Unit,
    showWeekdayAverages: Boolean = true,
) {
    val analytics = uiState.usageAnalytics
    AnimatedVisibility(
        visible = analyticsSelectedIsToday,
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(220),
        ) + fadeIn(animationSpec = tween(140)),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(180),
        ) + fadeOut(animationSpec = tween(100)),
    ) {
        TodayBlockingControls(
            uiState = uiState,
            isBlockingActive = isBlockingActive,
            isPauseActive = isPauseActive,
            pauseRemainingMillis = pauseRemainingMillis,
            onBlockOptionSelected = onBlockOptionSelected,
            onConfigureDailyLimit = onConfigureDailyLimit,
            onIntervalTimerClick = onIntervalTimerClick,
            onIntervalTimerEdit = onIntervalTimerEdit,
            onPauseToggle = onPauseToggle,
        )
    }

    InlineUsageAnalyticsPanel(
        analytics = analytics,
        sessionChunksExpanded = sessionChunksExpanded,
        onToggleSessionChunks = onToggleSessionChunks,
    )

    if (analyticsSelectedIsToday && showWeekdayAverages) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
        WeekdayAverageSection(
            weekdayAverages = analytics.weekdayAverages,
            selectedPeriod = uiState.averagePeriod,
            referenceLimitMillis = uiState.weekdayReferenceMillis,
            onPeriodSelected = { onAveragePeriodSelected(it) },
        )
    }
}
