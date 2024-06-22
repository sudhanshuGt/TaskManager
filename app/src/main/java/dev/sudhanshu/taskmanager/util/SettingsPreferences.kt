package dev.sudhanshu.taskmanager.util



import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val NOTIFICATIONS_ENABLED = "notifications_enabled"

        @Volatile
        private var INSTANCE: SettingsPreferences? = null

        fun getInstance(context: Context): SettingsPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsPreferences(context).also { INSTANCE = it }
            }
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_MODE_ENABLED, enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_MODE_ENABLED, false)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, false)
    }
}
