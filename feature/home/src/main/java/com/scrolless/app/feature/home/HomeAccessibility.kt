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

import android.accessibilityservice.AccessibilityService
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import timber.log.Timber

internal class HomeAccessibilityState(
    val showExplainer: Boolean,
    val showSuccess: Boolean,
    val showExplainerPrompt: () -> Unit,
    val dismissExplainer: () -> Unit,
    val dismissSuccess: () -> Unit,
)

@Composable
internal fun rememberHomeAccessibilityState(
    context: Context,
    uiState: HomeUiState,
    accessibilityServiceClass: Class<out AccessibilityService>?,
    viewModel: HomeViewModel,
): HomeAccessibilityState {
    var showAccessibilityExplainer by remember { mutableStateOf(false) }
    var showAccessibilitySuccess by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    fun showAccessibilityExplainerPrompt() {
        if (showAccessibilityExplainer) return
        Timber.d("Set waiting for accessibility for app auto open")
        viewModel.setWaitingForAccessibility(true)
        showAccessibilityExplainer = true
        if (!uiState.hasSeenAccessibilityExplainer) {
            viewModel.onAccessibilityExplainerShown()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Timber.d("HomeScreen resumed")
                val isAccessibilityEnabled = context.isAccessibilityServiceEnabled(accessibilityServiceClass)
                if (isAccessibilityEnabled) {
                    if (showAccessibilityExplainer) {
                        Timber.i("Accessibility service enabled - showing success dialog")
                        showAccessibilityExplainer = false
                        showAccessibilitySuccess = true
                        viewModel.setWaitingForAccessibility(false)
                    }
                } else if (uiState.hasLoadedSettings) {
                    val hasBlockSelection = uiState.blockOption != com.scrolless.app.core.model.BlockOption.NothingSelected
                    val hasSeenExplainer = uiState.hasSeenAccessibilityExplainer
                    if ((!hasSeenExplainer || hasBlockSelection) && !showAccessibilityExplainer) {
                        Timber.i(
                            "Accessibility service disabled on resume - auto showing explainer (firstLaunch=%s, hasBlock=%s)",
                            !hasSeenExplainer,
                            hasBlockSelection,
                        )
                        showAccessibilityExplainerPrompt()
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.hasLoadedSettings, uiState.hasSeenAccessibilityExplainer, uiState.blockOption) {
        if (
            uiState.hasLoadedSettings &&
            !showAccessibilityExplainer &&
            !context.isAccessibilityServiceEnabled(accessibilityServiceClass)
        ) {
            when {
                !uiState.hasSeenAccessibilityExplainer -> {
                    Timber.i("First launch detected - showing accessibility explainer")
                    showAccessibilityExplainerPrompt()
                }

                uiState.blockOption != com.scrolless.app.core.model.BlockOption.NothingSelected -> {
                    Timber.i("Block option selected while accessibility disabled - showing explainer")
                    showAccessibilityExplainerPrompt()
                }
            }
        }
    }

    return HomeAccessibilityState(
        showExplainer = showAccessibilityExplainer,
        showSuccess = showAccessibilitySuccess,
        showExplainerPrompt = ::showAccessibilityExplainerPrompt,
        dismissExplainer = {
            Timber.d("AccessibilityExplainer: Dismiss from home screen")
            showAccessibilityExplainer = false
            viewModel.setWaitingForAccessibility(false)
        },
        dismissSuccess = { showAccessibilitySuccess = false },
    )
}
