package com.pqc.qr.utils.preferenceProvider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * A utility class for managing SharedPreferences in the application.
 * Wraps the default SharedPreferences using PreferenceManager for easy access.
 * Created by Jeetesh Surana.
 */
class PreferenceProvider(context: Context) {
    private val appContext: Context

    init {
        appContext = context.applicationContext
    }

    private val preference: SharedPreferences
        private get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun putString(key: String?, value: String?) {
        preference.edit().putString(key, value).apply()
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return preference.getString(key, defaultValue)
    }

    fun putBoolean(key: String?, value: Boolean) {
        preference.edit().putBoolean(key, value).apply()
    }

    fun putFloat(key: String?, value: Float) {
        preference.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return preference.getFloat(key, defaultValue)
    }

    fun getBoolean(key: String?): Boolean {
        return preference.getBoolean(key, false)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return preference.getBoolean(key, defaultValue)
    }

    fun clearPref() {
        preference.edit().clear().apply()
    }
}
