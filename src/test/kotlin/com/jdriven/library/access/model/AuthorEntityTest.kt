package com.jdriven.library.access.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.temporal.JulianFields
import kotlin.test.assertEquals


class AuthorEntityTest {

    @Test
    fun testEquals() {
        val a1 = AuthorEntity()
        val a2 = AuthorEntity()

        assertTrue(a1.equals(a1))
        assertFalse(a1.equals(a2))

        val u1 = UserEntity()
        assertFalse(a1.equals(u1))

        a1.id = 1
        a2.id = 2
        assertFalse(a1.equals(a2))

        a2.id = 1
        assertTrue(a1.equals(a2))
    }

    @Test
    fun testHascode() {
        val a1 = AuthorEntity()
        val a2 = AuthorEntity()
        assertEquals(a1.hashCode(), a2.hashCode())

        a1.id = 1
        a2.id = 1
        assertEquals(a1.hashCode(), a2.hashCode())

        a2.id = 2
        assertFalse(a1.equals(a2))
    }

    @Test
    fun qqqq() {
        val now = qqqq(LocalDate.now())
        qqqq(LocalDate.of(1858, 11, 17))
        qqqq(LocalDate.of(1800, 1, 1))

        val t0 = qqqq(LocalDate.of(1969, 8, 30))
        val t1 = qqqq(LocalDate.of(2025, 8, 30))
        println("t1 - t0 = ${t1 - t0}, % 7 = ${(t1 - t0) % 7}")
        println("t1 - now = ${t1 - now}, % 7 = ${(t1 - now) % 7}")
    }

    fun qqqq(date: LocalDate): Long {
        val mjd = date.getLong(JulianFields.MODIFIED_JULIAN_DAY)
        println("date=$date, mjd=$mjd")
        return mjd
    }
}