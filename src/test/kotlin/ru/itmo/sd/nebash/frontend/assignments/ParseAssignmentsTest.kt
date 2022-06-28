package ru.itmo.sd.nebash.frontend.assignments

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.itmo.sd.nebash.*

class ParseAssignmentsTest {

    private val a = "1 2"
    private val bb = " qw "
    private val state: State = MutableState(
        "a" to a, "bb" to bb,
    )

    @Test
    fun `one assignment`() {
        val expected = RawAssignmentStmt(
            list = listOf(Assignment("a".toVn(), " 1 2".vv))
        )
        assertEquals(expected to "", " a=\' 1 2\'".parseAssignments(state))
    }

    @Test
    fun export() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(Assignment("_ab1".toVn(), "| 11 22 ".vv))
        )
        assertEquals(expected to "tail", " export  _ab1=\"| 11 22 \"\t tail".parseAssignments(state))
    }

    @Test
    fun `multiple assignments`() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(
                Assignment("_1".toVn(), "| |".vv),
                Assignment("q".toVn(), "12".vv),
                Assignment("qwerty".toVn(), " | | ".vv)
            )
        ) to "echo \'arg\' | wc"
        assertEquals(
            expected,
            " \t_1=\'| |\' export q=12  qwerty=\" | | \" \t echo 'arg' | wc".parseAssignments(state)
        )
    }

    @Test
    fun `empty assignment`() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(
                Assignment("_1".toVn(), "| |".vv),
                Assignment("q".toVn(), "".vv),
                Assignment("qwerty".toVn(), "".vv)
            )
        ) to "echo \'arg\' | wc"
        assertEquals(
            expected,
            " \t_1=\'| |\' export q=  qwerty=\"\" \t echo 'arg' | wc".parseAssignments(state)
        )
    }

    @Test
    fun substitution() {
        val expected = RawAssignmentStmt(
            export = true,
            list = listOf(
                Assignment("_1".toVn(), "| \$a |".vv),
                Assignment("q".toVn(), " qw ".vv),
                Assignment("qwerty".toVn(), " $a".vv)
            )
        ) to "echo \'arg\' | wc"
        assertEquals(
            expected,
            " \t_1=\'| \$a |\' export q=\$bb  qwerty=\"\$q \$a\" \t echo 'arg' | wc".parseAssignments(state)
        )
    }
}
