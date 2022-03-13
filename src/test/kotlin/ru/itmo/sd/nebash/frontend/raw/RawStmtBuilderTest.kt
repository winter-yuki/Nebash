package ru.itmo.sd.nebash.frontend.raw

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RawStmtBuilderTest {

    @Test
    fun empty() {
        val builder = RawStmtBuilder()
        assertTrue(builder.isEmpty())
        assertTrue(builder.append("").isEmpty())
    }

    @Test
    fun `single line`() {
        val builder = RawStmtBuilder()
        val s = """echo 'hello word' "1 2""""
        builder.append(s)
        assertFalse(builder.isEmpty())
        assertEquals(s, builder.buildOrNull()?.stmt)
    }

    @Test
    fun `single quote`() {
        val builder = RawStmtBuilder()
        val s1 = "echo \'\n"
        builder.append(s1)
        assertNull(builder.buildOrNull())
        val s2 = " inside\n"
        builder.append(s2)
        assertNull(builder.buildOrNull())
        val s3 = "end\'"
        builder.append(s3)
        assertEquals(s1 + s2 + s3, builder.buildOrNull()?.stmt)
    }

    @Test
    fun `multiple quotes on a line`() {
        val builder = RawStmtBuilder()
        val s1 = "\'\"\"\'\"\n"
        builder.append(s1)
        assertNull(builder.buildOrNull())
        val s2 = "inner\n"
        builder.append(s2)
        assertNull(builder.buildOrNull())
        val s3 = "\"\'\'inline\'"
        builder.append(s3)
        assertNull(builder.buildOrNull())
        val s4 = "\'"
        builder.append(s4)
        assertEquals(s1 + s2 + s3 + s4, builder.buildOrNull()?.stmt)
    }

    @Test
    fun `one backslash`() {
        val builder = RawStmtBuilder()
        val s1 = " \'q\'w \\\n"
        builder.append(s1)
        assertNull(builder.buildOrNull())
        val s2 = "\"\'\"end"
        builder.append(s2)
        assertEquals(s1.dropLast(2) + '\n' + s2, builder.buildOrNull()?.stmt)
    }

    @Test
    fun `multiple backslashes`() {
        val builder = RawStmtBuilder()
        val s1 = "\tq \""
        builder.append(s1)
        assertNull(builder.buildOrNull())
        val s2 = "\"\"\'1\\2\""
        builder.append(s2)
        assertEquals(s1 + s2, builder.buildOrNull()?.stmt)
    }
}
