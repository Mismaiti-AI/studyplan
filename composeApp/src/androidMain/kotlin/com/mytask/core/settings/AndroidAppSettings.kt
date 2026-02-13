package com.mytask.core.settings

import android.content.Context
import androidx.core.content.edit

class AndroidAppSettings(context: Context) : AppSettings {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun getString(key: String, default: String): String =
        prefs.getString(key, default) ?: default

    override fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    override fun getInt(key: String, default: Int): Int =
        prefs.getInt(key, default)

    override fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    override fun remove(key: String) {
        prefs.edit { remove(key) }
    }
}