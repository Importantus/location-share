package digital.fischers.locationshare.data.keyValueStorage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    companion object {
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    }
}