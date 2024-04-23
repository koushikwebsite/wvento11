package com.wvt.wvento.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wvt.wvento.util.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val selectedLocationType = stringPreferencesKey("location")
        val selectedLocationPos = intPreferencesKey("locPosition")
        val backOnline = booleanPreferencesKey("backOnline")
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveUserLocationType(
        evt_location: String,
        loc_position: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedLocationType] = evt_location
            preferences[PreferenceKeys.selectedLocationPos] = loc_position
        }
        Log.d("DataStoreRepository", "entered into saveUserLocationType")
    }

    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    val readUserLocationType: Flow<UserLocationType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val selectedLocationType = preferences[PreferenceKeys.selectedLocationType] ?: ""
            val selectedLocationPos = preferences[PreferenceKeys.selectedLocationPos] ?: 0
            UserLocationType(
                selectedLocationType,
                selectedLocationPos
            )
        }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline
        }
}

data class UserLocationType(
    val selectedLocationType: String,
    val selectedLocationPos: Int
)

