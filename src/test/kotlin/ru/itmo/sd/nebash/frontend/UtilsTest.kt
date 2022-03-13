package ru.itmo.sd.nebash.frontend

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.itmo.sd.nebash.MutableState
import ru.itmo.sd.nebash.vn
import ru.itmo.sd.nebash.vv

class UtilsTest {

    private val a = "1"
    private val _a = "1 2"
    private val state = MutableState().apply {
        set("a".vn, a.vv)
        set("_a".vn, _a.vv)
    }

    @Test
    fun `no dollars`() {
        assertEquals("", "".substitute(state))
        val s = """Hello cruel 
            | world
            |:(
            |
        """.trimMargin()
        assertEquals(s, s.substitute(state))
    }

    @Test
    fun `dollar only`() {
        assertEquals("$", "$".substitute(state))
    }

    @Test
    fun `dollar with name only`() {
        assertEquals(a, "\$a".substitute(state))
        assertEquals(_a, "\$_a".substitute(state))
    }

    @Test
    fun `dollar first`() {
        assertEquals("$a \tend", "\$a \tend".substitute(state))
    }

    @Test
    fun `dollar last`() {
        val s1 = "hello$"
        assertEquals(s1, s1.substitute(state))
        val s2 = "hello\t$"
        assertEquals(s2, s2.substitute(state))
        assertEquals("$_a hello $", "\$_a hello $".substitute(state))
    }

    @Test
    fun `empty name`() {
        assertEquals("$ $ $a\t$", "$ $ \$a\t$".substitute(state))
    }

    @Test
    fun `multiple dollars`() {
        assertEquals("$a$_a", "\$a\$_a".substitute(state))
        assertEquals("$a 12$_a", "\$a 12\$_a".substitute(state))
        assertEquals("q$a 12$_a", "q\$a 12\$_a".substitute(state))
    }

    @Test
    fun `dollar dollar`() {
        assertEquals("$$", "$$".substitute(state))
        assertEquals("$a$$", "\$a$$".substitute(state))
    }

    @Test
    fun `no var in state`() {
        assertEquals("q w", "q\$q w\$w".substitute(state))
    }

    @Test
    fun `ignore quotes`() {
        assertEquals(""""$a '' " """, """"${'$'}a '' " """.substitute(state))
    }
}
