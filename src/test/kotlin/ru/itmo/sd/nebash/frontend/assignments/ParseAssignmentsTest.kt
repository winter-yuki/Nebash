package ru.itmo.sd.nebash.frontend.assignments

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.itmo.sd.nebash.*
import ru.itmo.sd.nebash.frontend.raw.rs

class ParseAssignmentsTest {

    private val a = "1 2"
    private val bb = " qw "
    private val state: State = MutableState(
        "a" to a, "bb" to bb,
    )

    @Test
    fun `one assignment`() {
        val expected = RawAssignmentStmt(
            list = listOf(Assignment("a".vn, " 1 2".vv))
        )
        assertEquals(expected to "", " a=\' 1 2\'".rs.parseAssignments(state))
    }

    @Test
    fun export() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(Assignment("_ab1".vn, "| 11 22 ".vv))
        )
        assertEquals(expected to "tail", " export  _ab1=\"| 11 22 \"\t tail".rs.parseAssignments(state))
    }

    @Test
    fun `multiple assignments`() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(
                Assignment("_1".vn, "| |".vv),
                Assignment("q".vn, "12".vv),
                Assignment("qwerty".vn, " | | ".vv)
            )
        ) to "echo \'arg\' | wc"
        assertEquals(
            expected,
            " \t_1=\'| |\' export q=12  qwerty=\" | | \" \t echo 'arg' | wc".rs.parseAssignments(state)
        )
    }

    @Test
    fun `empty assignment`() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(
                Assignment("_1".vn, "| |".vv),
                Assignment("q".vn, "".vv),
                Assignment("qwerty".vn, "".vv)
            )
        ) to "echo \'arg\' | wc"
        assertEquals(
            expected,
            " \t_1=\'| |\' export q=  qwerty=\"\" \t echo 'arg' | wc".rs.parseAssignments(state)
        )
    }

    @Test
    fun substitution() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(
                Assignment("_1".vn, "| \$a |".vv),
                Assignment("q".vn, " qw ".vv),
                Assignment("qwerty".vn, " $a".vv)
            )
        ) to "echo \'arg\' | wc"
        assertEquals(
            expected,
            " \t_1=\'| \$a |\' export q=\$bb  qwerty=\"\$q \$a\" \t echo 'arg' | wc".rs.parseAssignments(state)
        )
    }
}
