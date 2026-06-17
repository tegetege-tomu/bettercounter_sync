package org.kde.bettercounter.extensions

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kde.bettercounter.persistence.FirstDayOfWeek
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class CalendarExtensionTest {

    private lateinit var savedTimeZone: TimeZone

    @Before
    fun setUp() {
        savedTimeZone = TimeZone.getDefault()
        // Pin to a timezone where the UTC timestamps used below don't cross midnight,
        // so tests are not sensitive to the host machine's locale.
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))
    }

    @After
    fun tearDown() {
        FirstDayOfWeek.testingOverride = null
        TimeZone.setDefault(savedTimeZone)
    }

    @Test
    fun `week truncation with Sunday as first day of week`() {
        FirstDayOfWeek.testingOverride = Calendar.SUNDAY
        val sundayDate = Date(1697995920502).toCalendar().truncated(Calendar.WEEK_OF_YEAR) // Sunday 22 October 2023
        val mondayDate = Date(1691995920502).toCalendar().truncated(Calendar.WEEK_OF_YEAR) // Monday 14 August 2023
        // Sunday is the start of its own week, stays on 22
        Assert.assertEquals(22, sundayDate.get(Calendar.DAY_OF_MONTH))
        // Monday goes back to the preceding Sunday (13 August)
        Assert.assertEquals(13, mondayDate.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `week truncation with Monday as first day of week`() {
        FirstDayOfWeek.testingOverride = Calendar.MONDAY
        val sundayDate = Date(1697995920502).toCalendar().truncated(Calendar.WEEK_OF_YEAR) // Sunday 22 October 2023
        val mondayDate = Date(1691995920502).toCalendar().truncated(Calendar.WEEK_OF_YEAR) // Monday 14 August 2023
        // Sunday goes back to the preceding Monday (16 October)
        Assert.assertEquals(16, sundayDate.get(Calendar.DAY_OF_MONTH))
        // Monday is the start of its own week, stays on 14
        Assert.assertEquals(14, mondayDate.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `week truncation with Wednesday as first day of week`() {
        FirstDayOfWeek.testingOverride = Calendar.WEDNESDAY
        val sundayDate = Date(1697995920502).toCalendar().truncated(Calendar.WEEK_OF_YEAR) // Sunday 22 October 2023
        // Sunday goes back to the preceding Wednesday (18 October)
        Assert.assertEquals(18, sundayDate.get(Calendar.DAY_OF_MONTH))
    }
}
