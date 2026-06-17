package org.kde.bettercounter.extensions

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.TimeZone

class ChronoUnitExtensionTest {

    private lateinit var savedTimeZone: TimeZone

    @Before
    fun setUp() {
        savedTimeZone = TimeZone.getDefault()
        // Tests below use UTC timestamps where the date boundaries cross in CET (UTC+1).
        // Pin the timezone so the expected counts don't depend on the host machine.
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(savedTimeZone)
    }

    @Test
    fun `millis with less than one second diff`() {
        val from = Date(1697146570502) // 12 October 2023 21:36:10.502 UTC  (22:36 CET → Oct 12)
        val to = Date(1700780933742)   // 23 November 2023 23:08:53.742 UTC (00:08 CET → Nov 24)
        val count = ChronoUnit.DAYS.count(from, to)
        assertEquals(44, count)
    }

    @Test
    fun `millis with more than one second diff`() {
        val from = Date(1697146570502) // 12 October 2023 21:36:10.502 UTC  (22:36 CET → Oct 12)
        val to = Date(1700780934441)   // 23 November 2023 23:08:54.441 UTC (00:08 CET → Nov 24)
        val count = ChronoUnit.DAYS.count(from, to)
        assertEquals(44, count)
    }

    @Test
    fun `one second later is one day`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1700781096000) // 23 November 2023 23:11:36 UTC
        val count = ChronoUnit.DAYS.count(from, to)
        assertEquals(1, count)
    }

    @Test
    fun `one exact day later are two days`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1700867495000) // 24 November 2023 23:11:35 UTC
        val count = ChronoUnit.DAYS.count(from, to)
        assertEquals(2, count)
    }

    @Test
    fun `one second later is one week`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1700781096000) // 23 November 2023 23:11:36 UTC
        val count = ChronoUnit.WEEKS.count(from, to)
        assertEquals(1, count)
    }

    @Test
    fun `one exact week later are two weeks`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1701385895000) // 30 November 2023 23:11:35 UTC
        val count = ChronoUnit.WEEKS.count(from, to)
        assertEquals(2, count)
    }

    @Test
    fun `one second later is one month`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1700781096000) // 23 November 2023 23:11:36 UTC
        val count = ChronoUnit.MONTHS.count(from, to)
        assertEquals(1, count)
    }

    @Test
    fun `one exact month later are two months`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1703373095000) // 23 December 2023 23:11:35 UTC
        val count = ChronoUnit.MONTHS.count(from, to)
        assertEquals(2, count)
    }

    @Test
    fun `one second later is one year`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1700781096000) // 23 November 2023 23:11:36 UTC
        val count = ChronoUnit.YEARS.count(from, to)
        assertEquals(1, count)
    }

    @Test
    fun `one exact year later are two years`() {
        val from = Date(1700781095000) // 23 November 2023 23:11:35 UTC
        val to = Date(1732403495000) // 23 November 2024 23:11:35 UTC
        val count = ChronoUnit.YEARS.count(from, to)
        assertEquals(2, count)
    }
}
