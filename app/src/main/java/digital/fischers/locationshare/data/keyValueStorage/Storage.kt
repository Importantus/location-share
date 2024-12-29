package digital.fischers.locationshare.data.keyValueStorage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import digital.fischers.locationshare.data.keyValueStorage.entities.UserEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("storage")

class Storage(context: Context) {
    private val dataStore = context.dataStore

    suspend fun setLastSyncTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME] = time
        }
    }

    suspend fun getLastSyncTime(): Long {
        return dataStore.data.map { preferences ->
            preferences[LAST_SYNC_TIME] ?: 0L
        }.firstOrNull() ?: 0L
    }

    suspend fun setUser(user: UserEntity) {
        dataStore.edit { preferences ->
            preferences[USER] = user.toJSON()
        }
    }

    suspend fun getUser(): UserEntity? {
        return dataStore.data.map { preferences ->
            preferences[USER]?.let { UserEntity.fromJSONOrNull(it) }
        }.firstOrNull()
    }

    fun getUserStream() = dataStore.data.map { preferences ->
        preferences[USER]?.let { UserEntity.fromJSONOrNull(it) }
    }

    suspend fun setServerUrl(serverUrl: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = serverUrl
        }
    }

    suspend fun getServerUrl(): String {
        return dataStore.data.map { preferences ->
            preferences[SERVER_URL] ?: ""
        }.firstOrNull() ?: ""
    }

    fun getServerURLStream() = dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: ""
    }

    suspend fun getOnboardingStep(): Int {
        return dataStore.data.map { preferences ->
            preferences[ONBOARDING_STEP] ?: 0
        }.firstOrNull() ?: 0
    }

    fun getOnboardingStepStream() = dataStore.data.map { preferences ->
        preferences[ONBOARDING_STEP] ?: 0
    }

    suspend fun setOnboardingStep(step: Int) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_STEP] = step
        }
    }

    companion object {
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        private val USER = stringPreferencesKey("user")
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val ONBOARDING_STEP = intPreferencesKey("onboarding_step")
    }
}