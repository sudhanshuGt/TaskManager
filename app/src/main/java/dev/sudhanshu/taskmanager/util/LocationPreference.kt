package dev.sudhanshu.taskmanager.util



import android.content.Context
import android.content.SharedPreferences
import android.location.Location

class LocationPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "location_prefs"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val USER_ID = "user"
    }

    fun saveLocation(location: Location) {
        with(prefs.edit()) {
            putFloat(KEY_LATITUDE, location.latitude.toFloat())
            putFloat(KEY_LONGITUDE, location.longitude.toFloat())
            apply()
        }
    }

    fun saveUserId(userId : String){
        with(prefs.edit()) {
            putString(USER_ID, userId)
            apply()
        }
    }

    fun getUserId() : String? {
        return prefs.getString(USER_ID, "")
    }

    fun getLocation(): Location? {
        val latitude = prefs.getFloat(KEY_LATITUDE, Float.MIN_VALUE)
        val longitude = prefs.getFloat(KEY_LONGITUDE, Float.MIN_VALUE)
        return if (latitude != Float.MIN_VALUE && longitude != Float.MIN_VALUE) {
            Location("").apply {
                this.latitude = latitude.toDouble()
                this.longitude = longitude.toDouble()
            }
        } else {
            null
        }
    }
}
