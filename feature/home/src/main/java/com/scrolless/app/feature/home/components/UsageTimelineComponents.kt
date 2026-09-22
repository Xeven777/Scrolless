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
package com.scrolless.app.feature.home.components

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrolless.app.core.model.BlockableApp
import com.scrolless.app.core.model.SessionSegment
import com.scrolless.app.designsystem.component.AutoResizingText
import com.scrolless.app.designsystem.component.ScrollessCard
import com.scrolless.app.designsystem.icon.ScrollessIcons
import com.scrolless.app.designsystem.theme.ScrollessPillShape
import com.scrolless.app.designsystem.theme.ScrollessTheme
import com.scrolless.app.designsystem.theme.facebookColor
import com.scrolless.app.designsystem.theme.facebookLiteColor
import com.scrolless.app.designsystem.theme.instagramReelsColor
import com.scrolless.app.designsystem.theme.snapchatColor
import com.scrolless.app.designsystem.theme.spacing
import com.scrolless.app.designsystem.theme.tiktokColor
import com.scrolless.app.designsystem.theme.tiktokLiteColor
import com.scrolless.app.designsystem.theme.youtubeShortsColor
import com.scrolless.app.feature.home.AppUsageTotal
import com.scrolless.app.feature.home.R
import com.scrolless.app.feature.home.UsageAnalyticsDayUiState
import com.scrolless.app.feature.home.UsageAnalyticsUiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

val ANALYTICS_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
val ANALYTICS_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun InlineUsageAnalyticsPanel(analytics: UsageAnalyticsUiState, sessionChunksExpanded: Boolean, onToggleSessionChunks: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large + 2.dp),
    ) {
        UsageAnalyticsDayPage(
            analytics = analytics,
            sessionChunksExpanded = sessionChunksExpanded,
            onToggleSessionChunks = onToggleSessionChunks,
        )
    }
}

@Composable
fun UsageAnalyticsDayPage(
    analytics: UsageAnalyticsUiState,
    sessionChunksExpanded: Boolean = false,
    onToggleSessionChunks: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large + 2.dp),
    ) {
        UsageTimelineSection(
            analytics = analytics,
            sessionChunksExpanded = sessionChunksExpanded,
            onToggleSessionChunks = onToggleSessionChunks,
        )
    }
}

@Composable
fun UsageTimelineSection(analytics: UsageAnalyticsUiState, sessionChunksExpanded: Boolean, onToggleSessionChunks: () -> Unit) {
    val sessionSegments = analytics.sessionSegments
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small + 2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(
                    enabled = sessionSegments.isNotEmpty(),
                    onClick = onToggleSessionChunks,
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.usage_analytics_timeline_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (sessionSegments.isNotEmpty()) {
                    Surface(
                        shape = ScrollessPillShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.usage_analytics_session_count,
                                    sessionSegments.size,
                                    sessionSegments.size,
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            )

                            Text(
                                text = analytics.dailyTotalMillis.formatAnalyticsDuration(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                if (sessionSegments.isNotEmpty()) {
                    val iconRotation by animateFloatAsState(
                        targetValue = if (sessionChunksExpanded) 180f else 0f,
                        animationSpec = tween(durationMillis = 260),
                        label = "expandIconRotation",
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = if (sessionChunksExpanded) {
                                stringResource(R.string.usage_analytics_collapse)
                            } else {
                                stringResource(R.string.usage_analytics_expand)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Icon(
                            painter = painterResource(id = ScrollessIcons.ArrowDropDown),
                            contentDescription = if (sessionChunksExpanded) {
                                stringResource(R.string.usage_analytics_collapse)
                            } else {
                                stringResource(R.string.usage_analytics_expand)
                            },
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .width(20.dp)
                                .rotate(iconRotation),
                        )
                    }
                }
            }
        }
        ScrollessCard(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.large),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium + 2.dp),
            ) {
                UsageTimelineCanvas(
                    sessionSegments = sessionSegments,
                    selectedDate = analytics.selectedDate,
                )
                if (sessionSegments.isEmpty()) {
                    Text(
                        text = stringResource(R.string.usage_analytics_timeline_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (sessionSegments.isNotEmpty()) {
                    AnimatedVisibility(
                        visible = sessionChunksExpanded,
                        enter = fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(260)),
                        exit = fadeOut(animationSpec = tween(120)) + shrinkVertically(animationSpec = tween(200)),
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            sessionSegments.forEach { segment ->
                                SessionChunkRow(segment = segment)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UsageTimelineCanvas(sessionSegments: List<SessionSegment>, selectedDate: LocalDate) {
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val trackColor = MaterialTheme.colorScheme.surface

    val motionScheme = MaterialTheme.motionScheme
    val transitionProgress = remember(selectedDate) { Animatable(0f) }
    LaunchedEffect(selectedDate) {
        transitionProgress.animateTo(
            targetValue = 1f,
            animationSpec = motionScheme.slowSpatialSpec(),
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            val trackTop = size.height * 0.28f
            val trackHeight = size.height * 0.44f
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, trackTop),
                size = Size(size.width, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f),
            )

            for (hour in 0..24 step 6) {
                val x = size.width * (hour / 24f)
                drawLine(
                    color = outline,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            sessionSegments.forEachIndexed { index, segment ->
                val progress = (transitionProgress.value * (1f + index * 0.05f) - index * 0.05f).coerceAtLeast(0f)
                val startMinutes = segment.startDateTime.toLocalTime().toSecondOfDay() / 60f
                val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(segment.durationMillis).coerceAtLeast(1).toFloat()
                val startX = size.width * (startMinutes / 1440f)
                val width = (size.width * (durationMinutes / 1440f)).coerceAtLeast(4.dp.toPx())

                val animatedHeight = trackHeight * progress
                val animatedTop = trackTop + (trackHeight - animatedHeight) / 2f

                drawRoundRect(
                    color = segment.app.analyticsColor(),
                    topLeft = Offset(startX, animatedTop),
                    size = Size(width.coerceAtMost(size.width - startX), animatedHeight),
                    cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f),
                )
            }
        }
        TimelineTickLabels()
    }
}

@Composable
fun TimelineTickLabels() {
    val context = LocalContext.current
    val is24Hour = DateFormat.is24HourFormat(context)
    val locale = Locale.current.platformLocale

    // Hardcoded "00:00" ticks ignored the device's 12/24-hour setting and the locale,
    // so the axis disagreed with every other time in the app.
    val tickLabels = remember(is24Hour, locale) {
        val formatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm a", locale)
        val startOfNextDay = if (is24Hour) {
            "24:00"
        } else {
            LocalTime.of(0, 0).format(formatter)
        }
        listOf(0, 6, 12, 18).map { hour -> LocalTime.of(hour, 0).format(formatter) } + startOfNextDay
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        tickLabels.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun SessionChunkRow(segment: SessionSegment) {
    val startTime = segment.startDateTime.toLocalTime()
    val endTime = segment.startDateTime.plusNanos(TimeUnit.MILLISECONDS.toNanos(segment.durationMillis)).toLocalTime()
    Row(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(42.dp)
                .background(segment.app.analyticsColor(), RoundedCornerShape(4.dp)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = segment.app.analyticsDisplayName(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(
                    R.string.usage_analytics_time_range,
                    startTime.format(ANALYTICS_TIME_FORMATTER),
                    endTime.format(ANALYTICS_TIME_FORMATTER),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = segment.app.analyticsColor().copy(alpha = 0.12f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            AutoResizingText(
                text = segment.durationMillis.formatAnalyticsDuration(),
                modifier = Modifier
                    .widthIn(min = 54.dp, max = 92.dp)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minFontSize = 10.sp,
            )
        }
    }
}

// --- Analytics utility functions ---

fun Long.formatAnalyticsDuration(): String {
    val totalSeconds = (this / 1_000L).coerceAtLeast(0L)
    val totalMinutes = totalSeconds / 60L
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    val seconds = totalSeconds % 60L

    return when {
        hours > 0L && minutes > 0L -> "${hours}h ${minutes}m"
        hours > 0L -> "${hours}h"
        minutes > 0L -> "${minutes}m"
        else -> "${seconds}s"
    }
}

fun analyticsForDate(analytics: UsageAnalyticsUiState, date: LocalDate): UsageAnalyticsUiState {
    val dayAnalytics = analytics.daySummaries[date] ?: UsageAnalyticsDayUiState(date = date)
    return analytics.copy(
        selectedDate = date,
        dailyTotalMillis = dayAnalytics.dailyTotalMillis,
        sessionSegments = dayAnalytics.sessionSegments,
        appTotals = dayAnalytics.appTotals,
        canNavigateNext = date.isBefore(analytics.today),
    )
}

fun BlockableApp.displayName(context: Context): String = context.getString(
    when (this) {
        BlockableApp.FACEBOOK -> R.string.app_facebook
        BlockableApp.FACEBOOK_LITE -> R.string.app_facebook_lite
        BlockableApp.REELS -> R.string.app_reels
        BlockableApp.SNAPCHAT -> R.string.app_snapchat
        BlockableApp.SHORTS -> R.string.app_shorts
        BlockableApp.TIKTOK -> R.string.app_tiktok
        BlockableApp.TIKTOK_LITE -> R.string.app_tiktok_lite
    },
)

@Composable
fun BlockableApp.analyticsDisplayName(): String = displayName(LocalContext.current)

fun BlockableApp.analyticsColor(): Color = when (this) {
    BlockableApp.FACEBOOK -> facebookColor
    BlockableApp.FACEBOOK_LITE -> facebookLiteColor
    BlockableApp.REELS -> instagramReelsColor
    BlockableApp.SNAPCHAT -> snapchatColor
    BlockableApp.SHORTS -> youtubeShortsColor
    BlockableApp.TIKTOK -> tiktokColor
    BlockableApp.TIKTOK_LITE -> tiktokLiteColor
}

// --- Analytics previews ---

@Preview(name = "Usage Analytics Previous Day")
@Composable
private fun PreviewUsageAnalyticsPreviousDay() {
    ScrollessTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                InlineUsageAnalyticsPanel(
                    analytics = previewUsageAnalytics(populated = true),
                    sessionChunksExpanded = false,
                    onToggleSessionChunks = {},
                )
            }
        }
    }
}

@Preview(name = "Usage Analytics Expanded")
@Composable
private fun PreviewUsageAnalyticsExpanded() {
    ScrollessTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                InlineUsageAnalyticsPanel(
                    analytics = previewUsageAnalytics(populated = true),
                    sessionChunksExpanded = true,
                    onToggleSessionChunks = {},
                )
            }
        }
    }
}

@Preview(name = "Usage Analytics Empty")
@Composable
private fun PreviewUsageAnalyticsEmpty() {
    ScrollessTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                InlineUsageAnalyticsPanel(
                    analytics = previewUsageAnalytics(populated = false),
                    sessionChunksExpanded = false,
                    onToggleSessionChunks = {},
                )
            }
        }
    }
}

private fun previewUsageAnalytics(populated: Boolean): UsageAnalyticsUiState {
    val date = LocalDate.of(2026, 5, 26)
    val segments = if (!populated) {
        emptyList()
    } else {
        listOf(
            SessionSegment(BlockableApp.REELS, TimeUnit.MINUTES.toMillis(18), date.atTime(8, 10)),
            SessionSegment(BlockableApp.SHORTS, TimeUnit.MINUTES.toMillis(12), date.atTime(12, 35)),
            SessionSegment(BlockableApp.TIKTOK, TimeUnit.MINUTES.toMillis(22), date.atTime(19, 20)),
            SessionSegment(BlockableApp.REELS, TimeUnit.MINUTES.toMillis(9), date.atTime(22, 5)),
        )
    }
    val appTotals = segments.groupBy { it.app }.map { (app, appSegments) ->
        AppUsageTotal(app = app, totalMillis = appSegments.sumOf { it.durationMillis })
    }.sortedByDescending { it.totalMillis }
    return UsageAnalyticsUiState(
        selectedDate = date,
        today = date,
        dailyTotalMillis = segments.sumOf { it.durationMillis },
        sessionSegments = segments,
        appTotals = appTotals,
        weekdayAverages = emptyList(),
    )
}
