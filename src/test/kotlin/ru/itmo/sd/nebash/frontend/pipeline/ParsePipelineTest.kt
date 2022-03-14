package ru.itmo.sd.nebash.frontend.pipeline

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.itmo.sd.nebash.MutableState
import ru.itmo.sd.nebash.PipelineAtom
import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.backend.ca
import ru.itmo.sd.nebash.backend.cn
import ru.itmo.sd.nebash.frontend.EmptyPipelineAtomException

class ParsePipelineTest {

    private val a = "1"
    private val b = "23"
    private val ec = "ec"
    private val ho = "ho arg"
    private val state: State = MutableState(
        "a" to a, "b" to b, "ec" to ec, "ho" to ho
    )

    @Test
    fun empty() {
        assertEquals(listOf<PipelineAtom>(), "".parsePipeline(state))
    }

    @Test
    fun `command name`() {
        val expected = listOf(PipelineAtom(name = "wc".cn))
        assertEquals(expected, "wc ".parsePipeline(state))
    }

    @Test
    fun `command with args`() {
        val expected = listOf(
            PipelineAtom(
                name = "echo".cn,
                args = listOf("fi rst ".ca, "second".ca, " t h\ti\tr d ".ca)
            )
        )
        val actual = " echo \'fi rst \' second \" t h\ti\tr d \"".parsePipeline(state)
        assertEquals(expected, actual)
    }

    @Test
    fun substitute() {
        val expected = listOf(
            PipelineAtom(
                name = "echo".cn,
                args = listOf("arg".ca, "fi \$rst ".ca, "second".ca, "$a t h\ti\tr d ".ca)
            )
        )
        val stmt = " \$ec\$ho \'fi \$rst \' second \"\$a t h\ti\tr d \""
        assertEquals(expected, stmt.parsePipeline(state))
    }

    @Test
    fun pipeline() {
        val expected = listOf(
            PipelineAtom(
                name = "echo".cn,
                args = listOf("arg".ca, "fi \$rst |".ca, "second".ca, "$a t h\ti\tr d ".ca)
            ),
            PipelineAtom(
                name = "wc".cn,
                args = listOf()
            )
        )
        val stmt = " \$ec\$ho \'fi \$rst |\' second \"\$a t h\ti\tr d \"| wc"
        assertEquals(expected, stmt.parsePipeline(state))
    }

    @Test
    fun `empty pipeline atom`() {
        val stmt1 = " ||\$ec\$ho \'fi \$rst |\' second \"\$a t h\ti\tr d \"| wc"
        assertThrows<EmptyPipelineAtomException> { stmt1.parsePipeline(state) }
        val stmt2 = " \$ec\$ho \'fi \$rst |\' second \"\$a t h\ti\tr d \"| wc|"
        assertThrows<EmptyPipelineAtomException> { stmt2.parsePipeline(state) }
    }
}
