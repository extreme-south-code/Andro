package com.example.myapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    object Home: BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Feed: BottomBarScreen(
        route = "feed",
        title = "Feed",
        icon = Icons.Default.Star
    )
    object Category: BottomBarScreen(
        route = "category",
        title = "Category",
        icon = Icons.Default.List
    )
}