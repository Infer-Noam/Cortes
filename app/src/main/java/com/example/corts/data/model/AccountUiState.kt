package com.example.corts.data.model

import com.example.corts.ui.panes.account.LEAGUE
import com.mapbox.maps.MapboxExperimental


data class AccountUiState @OptIn(MapboxExperimental::class) constructor(
    // val picture: Picture
    val name: String = "",
    val userName: String= "",
    val signUpDate: String = "",
    val country: String = "",
    // google user or smth
    // array of people you follow
    // array of people that follow you
    val xp: Int = 0,
    val league: LEAGUE = LEAGUE.BRONZE,
    val leagueRank: Int = 0,
    val streak: Int = 0,
)