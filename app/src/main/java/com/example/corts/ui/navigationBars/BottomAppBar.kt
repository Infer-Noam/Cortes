package com.example.corts.ui.navigationBars

import androidx.compose.material3.BottomAppBarState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.corts.R

@Composable
fun BottomAppBar(){
    NavigationBar { // using navigation bar for bottom navigation
        NavigationBarItem( // item in the navigation bar
            icon = { // icon
                Icon(
                    painter = painterResource(
                        R.drawable.baseline_settings_24
                    ),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            label = { Text("Settings") },
            selected = true,// selectedScreen == SCREENS.SAVED_TITLES, // icon is selected when selected screen is saved titles
            onClick = {
                // selectedScreen = SCREENS.SAVED_TITLES
            } // when icon is clicked the screen is saved titles
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.round_show_chart_24),
                    contentDescription = "statistics screen"
                )
            },
            label = { Text("Statistics") },
            selected = false, // icon is selected when selected screen is statistics
            onClick = {  } // when icon is clicked the screen is statistics
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.leaderboards),
                    contentDescription = "global"
                )
            },
            label = { Text("Global") },
            selected = false, // icon is selected when selected screen is statistics
            onClick = {  } // when icon is clicked the screen is statistics
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.account),
                    contentDescription = "account"
                )
            },
            label = { Text("Account") },
            selected = false, // icon is selected when selected screen is statistics
            onClick = {  } // when icon is clicked the screen is statistics
        )
    }
}