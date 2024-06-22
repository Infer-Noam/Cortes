package com.example.corts.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

enum class NavigationType { // The 3 different ways for the app to look based on the device type
    Compact,
    Medium,
    Expanded
}

fun determineNavigationType(windowSizeClass: WindowSizeClass) : NavigationType {
    val navigationType: NavigationType = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    NavigationType.Compact
                }

                WindowWidthSizeClass.Medium -> {
                    NavigationType.Medium
                }

                WindowWidthSizeClass.Expanded -> {
                    NavigationType.Expanded
                }
                else -> {
                    NavigationType.Compact
                }
            }
    return navigationType
}