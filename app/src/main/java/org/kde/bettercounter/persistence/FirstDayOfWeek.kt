package org.kde.bettercounter.persistence

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Calendar

object FirstDayOfWeek {

    lateinit var prefs: SharedPreferences

    // Set in unit tests to bypass SharedPreferences (Calendar.SUNDAY=1 … Calendar.SATURDAY=7)
    @JvmField
    var testingOverride: Int? = null

    fun init(application: Application) {
        prefs = application.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
    }

    fun set(calendarDayOfWeek: Int) {
        prefs.edit {
            putInt(FIRST_DAY_OF_WEEK_KEY, calendarDayOfWeek)
        }
    }

    // Returns a Calendar.DAY_OF_WEEK constant (Calendar.SUNDAY=1 … Calendar.SATURDAY=7)
    fun get(): Int = testingOverride
        ?: if (::prefs.isInitialized) prefs.getInt(FIRST_DAY_OF_WEEK_KEY, Calendar.MONDAY) else Calendar.MONDAY

    private const val SHARED_PREFS_NAME = "prefs"
    private const val FIRST_DAY_OF_WEEK_KEY = "first_day_of_week"
}
