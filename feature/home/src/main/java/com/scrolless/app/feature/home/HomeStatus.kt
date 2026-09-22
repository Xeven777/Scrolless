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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scrolless.app.core.model.BlockOption
import com.scrolless.app.designsystem.theme.UsageStatus
import com.scrolless.app.designsystem.theme.indicatorColor
import com.scrolless.app.designsystem.theme.spacing
import com.scrolless.app.designsystem.theme.usageStatusFor
import com.scrolless.app.designsystem.util.formatTime
import com.scrolless.app.designsystem.util.toCountdownLabel

/**
 * The single question the home screen exists to answer: *what is happening right now?*
 *
 * Previously that answer was spread across the ring (usage), the limit chip, the
 * selected mode button, the pause button and the background tint — five places, none of
 * which stated it in words. Resolving the state once here keeps the headline, the
 * background accent and the ring's status color from drifting apart.
 */
internal enum class HomeBlockingState {
    /** No mode selected, so nothing is being blocked. */
    NO_GOAL,

    /**
     * A limit mode is selected but its time has not been configured yet, so there is
     * nothing to measure against.
     */
    LIMIT_UNCONFIGURED,

    /** Block-all is on and healthily doing its job. */
    BLOCKING_ALL,

    /** A limit mode is active with headroom left. */
    WITHIN_LIMIT,

    /** A limit mode is active and the allowance is used up. */
    LIMIT_REACHED,

    /** Blocking is temporarily suspended by the user. */
    PAUSED,
}

/**
 * Whether blocking is currently doing its job, independent of the selected mode.
 *
 * Block-all blocks unconditionally; the limit modes only block once their allowance is
 * spent. Shared so the home screen's accent tint and the controls' expanded state can't
 * disagree about whether blocking is live.
 */
internal fun resolveIsBlockingActive(uiState: HomeUiState): Boolean = when (uiState.blockOption) {
    BlockOption.BlockAll -> true

    BlockOption.DailyLimit ->
        uiState.settings.dailyLimitMillis > 0 && uiState.currentUsage >= uiState.settings.dailyLimitMillis

    BlockOption.IntervalTimer ->
        uiState.settings.intervalAllowanceMillis > 0 &&
            uiState.intervalUsage.usageMillis >= uiState.settings.intervalAllowanceMillis

    BlockOption.NothingSelected -> false
}

/**
 * Daily-limit watermark for the weekly averages, or 0 when daily limit is not the
 * active mode — the section then falls back to its own default reference.
 */
internal val HomeUiState.weekdayReferenceMillis: Long
    get() = if (blockOption == BlockOption.DailyLimit) settings.dailyLimitMillis else 0L

/**
 * True when the active mode has a goal to measure against. A limit mode that is
 * selected before its time is configured counts as *not* set, so the UI never reports
 * "0m of 0m used".
 */
internal val HomeUiState.hasConfiguredGoal: Boolean
    get() = when (blockOption) {
        BlockOption.DailyLimit -> settings.dailyLimitMillis > 0L
        BlockOption.IntervalTimer -> settings.intervalAllowanceMillis > 0L
        BlockOption.NothingSelected, BlockOption.BlockAll -> false
    }

internal fun resolveHomeBlockingState(
    blockOption: BlockOption,
    isPauseActive: Boolean,
    isBlockingActive: Boolean,
    hasConfiguredGoal: Boolean,
): HomeBlockingState = when {
    isPauseActive -> HomeBlockingState.PAUSED
    blockOption == BlockOption.NothingSelected -> HomeBlockingState.NO_GOAL
    blockOption == BlockOption.BlockAll -> HomeBlockingState.BLOCKING_ALL
    !hasConfiguredGoal -> HomeBlockingState.LIMIT_UNCONFIGURED
    isBlockingActive -> HomeBlockingState.LIMIT_REACHED
    else -> HomeBlockingState.WITHIN_LIMIT
}

/**
 * Status the state should be tinted with, or null when there is no goal to measure
 * against. `WITHIN_LIMIT` intentionally defers to the caller-provided [usageStatus] so
 * the tint warms up as the limit approaches rather than flipping at the last moment.
 */
internal fun HomeBlockingState.tintStatus(usageStatus: UsageStatus): UsageStatus? = when (this) {
    HomeBlockingState.NO_GOAL, HomeBlockingState.LIMIT_UNCONFIGURED -> null
    HomeBlockingState.BLOCKING_ALL -> UsageStatus.COMFORTABLE
    HomeBlockingState.PAUSED -> UsageStatus.APPROACHING
    HomeBlockingState.WITHIN_LIMIT, HomeBlockingState.LIMIT_REACHED -> usageStatus
}

/**
 * Status line shown above the hero ring.
 *
 * Deliberately not a card: the screen already stacks several containers, and the one
 * sentence that explains everything should read as a caption under the title row rather
 * than compete with the analytics panels for visual weight.
 */
@Composable
internal fun HomeStatusHeadline(
    uiState: HomeUiState,
    state: HomeBlockingState,
    pauseRemainingMillis: Long,
    modifier: Modifier = Modifier,
) {
    val usageStatus = usageStatusFor(
        progressPercent = uiState.progress,
        hasGoal = uiState.hasConfiguredGoal,
    )
    val tint = state.tintStatus(usageStatus)

    val title = when (state) {
        HomeBlockingState.PAUSED -> stringResource(R.string.home_status_title_paused)

        HomeBlockingState.LIMIT_REACHED -> stringResource(R.string.home_status_title_limit_reached)

        HomeBlockingState.NO_GOAL -> stringResource(R.string.home_status_title_no_goal)

        HomeBlockingState.LIMIT_UNCONFIGURED -> stringResource(R.string.home_status_title_limit_unset)

        HomeBlockingState.BLOCKING_ALL, HomeBlockingState.WITHIN_LIMIT ->
            stringResource(R.string.home_status_title_blocking_on)
    }

    val supportParts: List<String> = when (state) {
        HomeBlockingState.PAUSED -> listOf(
            stringResource(
                R.string.home_status_support_resumes_in,
                pauseRemainingMillis.toCountdownLabel(),
            ),
        )

        HomeBlockingState.BLOCKING_ALL -> listOf(
            stringResource(R.string.home_status_support_block_all),
        )

        HomeBlockingState.NO_GOAL -> listOf(
            stringResource(R.string.home_status_support_choose_mode),
        )

        HomeBlockingState.LIMIT_UNCONFIGURED -> listOf(
            stringResource(R.string.home_status_support_limit_unset),
        )

        HomeBlockingState.WITHIN_LIMIT, HomeBlockingState.LIMIT_REACHED -> {
            val usedMillis = when (uiState.blockOption) {
                BlockOption.IntervalTimer -> uiState.intervalUsage.usageMillis
                else -> uiState.currentUsage
            }
            val limitMillis = when (uiState.blockOption) {
                BlockOption.IntervalTimer -> uiState.settings.intervalAllowanceMillis
                else -> uiState.settings.dailyLimitMillis
            }
            val remainingMillis = (limitMillis - usedMillis).coerceAtLeast(0L)
            listOfNotNull(
                stringResource(
                    R.string.home_status_support_usage_of_limit,
                    usedMillis.formatTime(),
                    limitMillis.formatTime(),
                ),
                // "0m left" is noise; the title already says the limit is reached.
                if (remainingMillis > 0L) {
                    stringResource(R.string.home_status_support_remaining, remainingMillis.formatTime())
                } else {
                    null
                },
            )
        }
    }

    val dotColor = tint?.indicatorColor ?: MaterialTheme.colorScheme.outline
    val supportText = supportParts.joinToString(separator = "  ·  ")

    Column(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = if (supportText.isEmpty()) title else "$title. $supportText"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = dotColor, shape = CircleShape),
            )
            AnimatedContent(
                targetState = title,
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(120))
                },
                label = "homeStatusTitle",
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        if (supportText.isNotEmpty()) {
            Text(
                text = supportText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
