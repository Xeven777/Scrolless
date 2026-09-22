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

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scrolless.app.core.model.BlockOption
import com.scrolless.app.core.model.IntervalUsage
import com.scrolless.app.designsystem.icon.ScrollessIcons
import com.scrolless.app.designsystem.theme.ScrollessPillShape
import com.scrolless.app.designsystem.theme.spacing
import com.scrolless.app.designsystem.util.rememberHapticHelper
import com.scrolless.app.feature.home.components.ProgressCard
import com.scrolless.app.feature.home.components.analyticsForDate
import com.scrolless.app.feature.home.components.shortLabel
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
internal fun UsageOverviewHeader(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    analytics: UsageAnalyticsUiState,
    blockingState: HomeBlockingState,
    pauseRemainingMillis: Long,
    pagerState: PagerState,
    todayPage: Int,
    selectedPage: Int,
    onUsageAnalyticsDateSelected: (LocalDate) -> Unit,
    onUsageAnalyticsTodaySelected: () -> Unit,
    onHelpClicked: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val isViewingToday = analytics.selectedDate == analytics.today

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            if (todayPage > 0) {
                DateNavigator(
                    selectedDate = analytics.selectedDate,
                    today = analytics.today,
                    canGoBack = selectedPage > 0,
                    canGoForward = selectedPage < todayPage,
                    onDateClick = onUsageAnalyticsTodaySelected,
                    onPrevious = {
                        val targetPage = (selectedPage - 1).coerceAtLeast(0)
                        onUsageAnalyticsDateSelected(analytics.dataStartDate.plusDays(targetPage.toLong()))
                    },
                    onNext = {
                        val targetPage = (selectedPage + 1).coerceAtMost(todayPage)
                        onUsageAnalyticsDateSelected(analytics.dataStartDate.plusDays(targetPage.toLong()))
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = MaterialTheme.spacing.small),
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = MaterialTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                HelpButton(
                    onClick = onHelpClicked,
                )

                SettingsButton(
                    onClick = onNavigateToSettings,
                )
            }
        }

        // A past day has no live blocking state, so the headline would be a lie there.
        if (isViewingToday) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            HomeStatusHeadline(
                uiState = uiState,
                state = blockingState,
                pauseRemainingMillis = pauseRemainingMillis,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            userScrollEnabled = false,
        ) { page ->
            val pageDate = remember(page, analytics.dataStartDate) {
                analytics.dataStartDate.plusDays(page.toLong())
            }
            val pageAnalytics = remember(pageDate, analytics.daySummaries) {
                analyticsForDate(analytics = analytics, date = pageDate)
            }
            val isTodayPage = pageDate == analytics.today

            ProgressCard(
                blockOption = if (isTodayPage) uiState.blockOption else BlockOption.NothingSelected,
                progress = if (isTodayPage) uiState.progress else 0,
                currentUsage = if (isTodayPage) uiState.currentUsage else pageAnalytics.dailyTotalMillis,
                limitMillis = if (isTodayPage) {
                    when (uiState.blockOption) {
                        BlockOption.DailyLimit -> uiState.settings.dailyLimitMillis
                        BlockOption.IntervalTimer -> uiState.settings.intervalAllowanceMillis
                        else -> 0L
                    }
                } else {
                    0L
                },
                intervalLengthMillis = if (isTodayPage) uiState.settings.intervalLengthMillis else 0L,
                intervalUsage = if (isTodayPage) uiState.intervalUsage else IntervalUsage.NOT_STARTED,
                listSessionSegments = if (isTodayPage) uiState.listSessionSegments else pageAnalytics.sessionSegments,
                onClick = onUsageAnalyticsTodaySelected,
            )
        }
    }
}

@Composable
private fun DateNavigator(
    selectedDate: LocalDate,
    today: LocalDate,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onDateClick: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticHelper = rememberHapticHelper()
    val dateLabel = if (selectedDate == today) {
        stringResource(R.string.usage_analytics_today)
    } else {
        selectedDate.formatHeaderDate()
    }

    // 48dp tall so the tappable date label clears the 48dp minimum target; the
    // stepper buttons inherit that from IconButton.
    Surface(
        modifier = modifier.height(48.dp),
        shape = ScrollessPillShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (canGoBack) {
                DateNavButton(
                    onClick = onPrevious,
                    icon = ScrollessIcons.ChevronLeft,
                    contentDescription = stringResource(R.string.usage_analytics_previous_day),
                )
            }
            AnimatedContent(
                targetState = dateLabel,
                label = "selectedDateLabel",
            ) { label ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(ScrollessPillShape)
                        .clickable(
                            enabled = selectedDate != today,
                            onClick = {
                                hapticHelper.playClick()
                                onDateClick()
                            },
                        )
                        .padding(horizontal = MaterialTheme.spacing.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.widthIn(max = 128.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (canGoForward) {
                DateNavButton(
                    onClick = onNext,
                    icon = ScrollessIcons.ChevronRight,
                    contentDescription = stringResource(R.string.usage_analytics_next_day),
                )
            }
        }
    }
}

@Composable
private fun DateNavButton(onClick: () -> Unit, @DrawableRes icon: Int, contentDescription: String) {
    val hapticHelper = rememberHapticHelper()
    IconButton(
        onClick = {
            hapticHelper.playClick()
            onClick()
        },
        modifier = Modifier.size(40.dp),
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun LocalDate.formatHeaderDate(): String {
    val locale = Locale.current.platformLocale
    val weekday = dayOfWeek.shortLabel()
    val monthStr = month.getDisplayName(TextStyle.SHORT, locale)
    val formattedWeekday = if (weekday.endsWith('.')) weekday else "$weekday."
    val formattedMonth = if (monthStr.endsWith('.')) monthStr else "$monthStr."
    return "$formattedWeekday, $formattedMonth $dayOfMonth"
}
