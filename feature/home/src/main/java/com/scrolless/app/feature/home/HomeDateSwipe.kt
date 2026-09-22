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

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.dateSwipeGesture(
    pagerState: PagerState,
    todayPage: Int,
    dateSwipeThresholdPx: Float,
    analytics: UsageAnalyticsUiState,
    selectedPage: Int,
    coroutineScope: CoroutineScope,
    onUsageAnalyticsDateSelected: (LocalDate) -> Unit,
): Modifier = this.pointerInput(
    pagerState,
    dateSwipeThresholdPx,
    selectedPage,
    todayPage,
    analytics.dataStartDate,
    analytics.selectedDate,
) {
    var totalDragX = 0f
    var dragStartPage = selectedPage
    detectHorizontalDragGestures(
        onDragStart = {
            totalDragX = 0f
            dragStartPage = selectedPage
        },
        onHorizontalDrag = { change, dragAmount ->
            change.consume()
            totalDragX += dragAmount
            pagerState.dispatchRawDelta(-dragAmount)
        },
        onDragEnd = {
            val targetPage = when {
                totalDragX <= -dateSwipeThresholdPx -> dragStartPage + 1
                totalDragX >= dateSwipeThresholdPx -> dragStartPage - 1
                else -> dragStartPage
            }.coerceIn(0, todayPage)
            val targetDate = analytics.dataStartDate.plusDays(targetPage.toLong())

            if (targetDate != analytics.selectedDate) {
                onUsageAnalyticsDateSelected(targetDate)
            }

            coroutineScope.launch {
                pagerState.animateScrollToPage(targetPage)
            }
        },
        onDragCancel = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(dragStartPage)
            }
        },
    )
}
