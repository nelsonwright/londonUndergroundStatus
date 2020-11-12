package uk.co.nelsonwright.londonundergroundstatus.shared

import android.content.Context

private const val PREFERENCES = "PREFERENCES"
private const val SPINNER_POSITION = "SPINNER_POSITION"

object SharedPrefsHelper {
    fun getSpinnerPosition(context: Context): Int {
        return context
            .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getInt(SPINNER_POSITION, 0)
    }

    fun saveSpinnerPosition(context: Context, position: Int) {
        context
            .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putInt(SPINNER_POSITION, position)
            .apply()
    }
}