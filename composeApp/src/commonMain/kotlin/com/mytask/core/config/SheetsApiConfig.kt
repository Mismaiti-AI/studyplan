package com.mytask.core.config

import com.mytask.core.settings.AppSettings

/**
 * Configurable Sheets API configuration.
 * Uses AppSettings from koin-di-skill (SharedPreferences/NSUserDefaults).
 *
 * Keys prefixed with "sheets_" to avoid conflicts with other features.
 */
class SheetsApiConfig(private val settings: AppSettings) {

    companion object {
        // Default URL from OAuth (can be empty if setup_deferred)
        // Generated from context.backend_config.config.script_url
        const val DEFAULT_URL = "https://script.google.com/macros/s/AKfycbwfXD_btU-9rMvETi1ue9NfJyRG7Mo91R9p04epR5PwYkjSd-md6LLpwMlAl2P79gO0/exec"

        // Spreadsheet URL for admin reference (optional, shown in settings)
        // Generated from context.backend_config.config.spreadsheet_url
        const val SPREADSHEET_URL = "https://docs.google.com/spreadsheets/d/1PyUHKJl3A77c2NLtew0196RBnK0mog9wjqjW6bC5scg/edit"

        const val REFRESH_INTERVAL_MINUTES = 30

        // Settings keys (prefixed to avoid conflicts)
        private const val KEY_SCRIPT_URL = "sheets_script_url"
    }

    var scriptUrl: String
        get() = settings.getString(KEY_SCRIPT_URL, DEFAULT_URL)
        set(value) = settings.putString(KEY_SCRIPT_URL, value)

    val isConfigured: Boolean
        get() = scriptUrl.isNotBlank()
}