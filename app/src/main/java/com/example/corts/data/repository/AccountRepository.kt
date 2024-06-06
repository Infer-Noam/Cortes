package com.example.corts.data.repository


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

import com.example.corts.data.utils.extentions.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject



interface AccountRepository {
    val name: Flow<String>
    val username: Flow<String>
    val signUpDate: Flow<String>
    val country: Flow<String>
    val xp: Flow<Int>
    val league: Flow<String>
    val leagueRank: Flow<Int>
    val streak: Flow<Int>



    suspend fun updateName(newName: String)

    suspend fun updateUsername(newUsername: String)

    suspend fun updateSignUpDate(newSignUpDate: String)

    suspend fun updateCountry(newCountry: String)

    suspend fun updateXp(newXp: Int)

    suspend fun updateLeague(newLeague: String)

    suspend fun updateLeagueRank(newLeagueRank: Int)

    suspend fun updateStreak(newStreak: Int)

}

class DefaultAccountRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : AccountRepository {

    companion object {
        val NAME_KEY = stringPreferencesKey("name")
        val USERNAME_KEY = stringPreferencesKey("username")
        val SIGNUPDATE_KEY = stringPreferencesKey("sign_up_date")
        val COUNTRY_KEY = stringPreferencesKey("country")
        val XP_KEY = intPreferencesKey("xp")
        val LEAGUE_RANK_KEY = intPreferencesKey("league_rank")
        val STREAK_KEY = intPreferencesKey("xp")
        val LEAGUE_KEY = stringPreferencesKey("league")

    }

    private val dataStore: DataStore<Preferences> = context.dataStore


    override val name: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[NAME_KEY] ?: ""
        }
    override suspend fun updateName(newName: String) {
        dataStore.edit { account ->
            account[NAME_KEY] = newName
        }
    }

    override val username: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }
    override suspend fun updateUsername(newUsername: String) {
        dataStore.edit { account ->
            account[USERNAME_KEY] = newUsername
        }
    }

    override val signUpDate: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[SIGNUPDATE_KEY] ?: ""
        }
    override suspend fun updateSignUpDate(newSignUpDate: String) {
        dataStore.edit { account ->
            account[SIGNUPDATE_KEY] = newSignUpDate
        }
    }

    override val country: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[COUNTRY_KEY] ?: ""
        }
    override suspend fun updateCountry(newCountry: String) {
        dataStore.edit { account ->
            account[COUNTRY_KEY] = newCountry
        }
    }
    override val xp: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[XP_KEY] ?: 0
        }
    override suspend fun updateXp(newXp: Int) {
        dataStore.edit { account ->
            account[XP_KEY] = newXp
        }
    }
    override val league: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[LEAGUE_KEY] ?: ""
        }
    override suspend fun updateLeague(newLeague: String) {
        dataStore.edit { account ->
            account[LEAGUE_KEY] = newLeague
        }
    }
    override suspend fun updateLeagueRank(newLeagueRank: Int) {
        dataStore.edit { account ->
            account[LEAGUE_RANK_KEY] = newLeagueRank
        }
    }
    override val leagueRank: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[LEAGUE_RANK_KEY] ?: 0
        }
    override suspend fun updateStreak(newStreak: Int) {
        dataStore.edit { account ->
            account[STREAK_KEY] = newStreak
        }
    }
    override val streak: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[STREAK_KEY] ?: 0
        }

}

