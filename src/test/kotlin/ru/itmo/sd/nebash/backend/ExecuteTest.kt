package ru.itmo.sd.nebash.backend

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.itmo.sd.nebash.*
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ExecuteTest {

    private inline fun <T> List<PipelineAtom>.test(
        stdin: String = "",
        stdout: T,
        stderr: String = "",
        state: MutableState = MutableState(),
        transform: (String) -> T,
    ) {
        val stdinPath = kotlin.io.path.createTempFile("stdin").apply { writeText(stdin) }
        val stdoutPath = kotlin.io.path.createTempFile("stdout")
        val stderrPath = kotlin.io.path.createTempFile("stderr")
        val stmt = PipelineStmt(pipeline = this, stdin = stdinPath, stdout = stdoutPath, stderr = stderrPath)
        stmt.execute(state)
        assertEquals(stdout, transform(stdoutPath.readText()))
        assertEquals(stderr, stderrPath.readText())
    }

    private fun parseWc(s: String): List<Int> =
        s.split("""\s+""".toRegex()).filter(String::isNotBlank).map(String::toInt)

    @Test
    fun assignment() {
        val state = MutableState()
        AssignmentStmt(assignments = listOf(Assignment("a".toVn(), "val".vv))).execute(state)
        assertEquals(state["a".toVn()], "val".vv)
        assertFalse("a".toVn() in state.env)
    }

    @Test
    fun `assignments with export`() {
        val state = MutableState()
        AssignmentStmt(
            export = true,
            assignments = listOf(
                Assignment("a".toVn(), "1".vv),
                Assignment("b".toVn(), "2".vv)
            )
        ).execute(state)
        assertEquals(state["a".toVn()], "1".vv)
        assertEquals(state["b".toVn()], "2".vv)
        assertTrue("a".toVn() in state.env)
        assertTrue("b".toVn() in state.env)
    }

    @Test
    fun cat() {
        listOf(PipelineAtom("cat".toCn())).test(
            stdin = "text in cat",
            stdout = "text in cat\n",
            transform = { it }
        )
        listOf(PipelineAtom("cat".toCn())).test(
            stdin = "text in cat\n next line",
            stdout = "text in cat\n next line\n",
            transform = { it }
        )
    }

    @Test
    fun echo() {
        listOf(PipelineAtom("echo".toCn(), listOf("hello world".ca, "second".ca))).test(
            stdin = "unused",
            stdout = "hello world second\n",
        ) { it }
    }

    @Test
    fun external() {
        PipelineStmt(pipeline = listOf(PipelineAtom("ls".toCn()))).execute(MutableState())
        assertThrows<FailToStartExternalProcess> {
            PipelineStmt(pipeline = listOf(PipelineAtom("qwertyuiop123456789".toCn()))).execute(MutableState())
        }
    }

    @Test
    fun pwd() {
        PipelineStmt(pipeline = listOf(PipelineAtom("pwd".toCn()))).execute(MutableState())
    }

    @Test
    fun wc() {
        listOf(PipelineAtom("wc".toCn())).test(
            stdin = " 2 words \t",
            stdout = listOf(1, 2, 11),
            transform = ::parseWc
        )
        listOf(PipelineAtom("wc".toCn())).test(
            stdin = " \t\n\n",
            stdout = listOf(2, 0, 4),
            transform = ::parseWc
        )
    }

    @Test
    fun `cats pipeline`() {
        val s = """
            | idwlq ndpsd kdqwpkd pqn\qw   mqkndk kd nd
            | qdqwnd oqnw
            | qwd
            |qwndi nqd] qwd nqwdnqw
            |
        """.trimMargin()
        listOf(
            PipelineAtom("cat".toCn()),
            PipelineAtom("cat".toCn()),
            PipelineAtom("cat".toCn())
        ).test(stdin = s, stdout = s) { it }
    }

    @Test
    fun `wc pipeline`() {
        val s = """
            | idwlq ndpsd kdqwpkd pqn\qw   mqkndk kd nd
            | qdqwnd oqnw
            | qwd
            |qwndi nqd] qwd nqwdnqw
            |
        """.trimMargin()
        listOf(
            PipelineAtom("cat".toCn()),
            PipelineAtom("cat".toCn()),
            PipelineAtom("cat".toCn()),
            PipelineAtom("wc".toCn())
        ).test(
            stdin = s,
            stdout = listOf(
                s.count { it == '\n' },
                s.split("""\s+""".toRegex()).count { it.isNotBlank() },
                s.length
            ),
            transform = ::parseWc
        )
    }
}
