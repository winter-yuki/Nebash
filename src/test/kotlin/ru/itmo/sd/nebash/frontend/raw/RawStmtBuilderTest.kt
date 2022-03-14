package ru.itmo.sd.nebash.frontend.raw

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RawStmtBuilderTest {

    @Test
    fun empty() {
        val builder = RawStmtBuilder()
        assertEquals(BuildResult.Empty, builder.build())
        builder.append("")
        assertEquals(BuildResult.Empty, builder.build())
        builder.append("\t\\\n")
        assertEquals(BuildResult.NotFinished, builder.build())
        builder.append("")
        assertEquals(BuildResult.Empty, builder.build())
    }

    @Test
    fun `single line`() {
        val builder = RawStmtBuilder()
        val s = """echo 'hello word' "1 2""""
        builder.append(s)
        assertEquals(BuildResult.Stmt(s.rs), builder.build())
    }

    @Test
    fun `single quote`() {
        val builder = RawStmtBuilder()
        val s1 = "echo \'\n"
        builder.append(s1)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s2 = " inside\n"
        builder.append(s2)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s3 = "end\'"
        builder.append(s3)
        assertEquals(BuildResult.Stmt((s1 + s2 + s3).rs), builder.build())
    }

    @Test
    fun `multiple quotes on a line`() {
        val builder = RawStmtBuilder()
        val s1 = "\'\"\"\'\"\n"
        builder.append(s1)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s2 = "inner\n"
        builder.append(s2)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s3 = "\"\'\'inline\'"
        builder.append(s3)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s4 = "\'"
        builder.append(s4)
        val expected = BuildResult.Stmt((s1 + s2 + s3 + s4).rs)
        assertEquals(expected, builder.build())
    }

    @Test
    fun `one backslash`() {
        val builder = RawStmtBuilder()
        val s1 = " \'q\'w \\\n"
        builder.append(s1)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s2 = "\"\'\"end"
        builder.append(s2)
        val expected = BuildResult.Stmt((s1.dropLast(2) + '\n' + s2).rs)
        assertEquals(expected, builder.build())
    }

    @Test
    fun `multiple backslashes`() {
        val builder = RawStmtBuilder()
        val s1 = "\tq \""
        builder.append(s1)
        assertEquals(BuildResult.NotFinished, builder.build())
        val s2 = "\"\"\'1\\2\""
        builder.append(s2)
        val expected = BuildResult.Stmt((s1 + s2).rs)
        assertEquals(expected, builder.build())
    }
}
