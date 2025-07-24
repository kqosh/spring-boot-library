package com.jdriven.library.access.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
}