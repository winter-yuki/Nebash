package ru.itmo.sd.nebash.backend

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.itmo.sd.nebash.*
import ru.itmo.sd.nebash.backend.commands.commandByName
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.IOException
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter

/**
 * Execute Nebash [Stmt].
 */
fun Stmt.execute(state: MutableState) = use { stdin, stdout, stderr ->
    try {
        when (this) {
            is AssignmentStmt -> eval(state)
            is PipelineStmt -> eval(state, stdin, stdout, stderr)
        }
    } catch (e: IOException) {
        throw IOExecutionException(e)
    } finally {
        stdout.flush()
        stderr.flush()
    }
}

private fun AssignmentStmt.eval(state: MutableState) {
    assignments.forEach { (name, value) ->
        state[name] = value
        if (export) state.export(name)
    }
}

private fun PipelineStmt.eval(
    state: State,
    stdin: BufferedReader,
    stdout: BufferedWriter,
    stderr: BufferedWriter
): Unit = runBlocking {
    val env = state.env + localAssignments.associate { (name, value) -> name to value }
    val stderrFlow = MutableSharedFlow<String>()
    val stdinFlow: Stdin = flow {
        while (true) {
            val input = stdin.readLine() ?: break
            val eof = "end"
            if (input == eof) break
            if (input.endsWith(eof)) {
                emit(input.dropLast(eof.length))
                break
            }
            emit(input + '\n')
        }
        emit(null)
    }.flowOn(Dispatchers.IO)

    launch {
        launch {
            stderrFlow.map { stderr.write(it) }.flowOn(Dispatchers.IO).collect()
        }
        val stdoutFlow = pipeline.fold(stdinFlow) { inFlow, (name, args) ->
            val cmd = commandByName(name)
            val outFlow = cmd(env, args, inFlow, stderrFlow)
            outFlow.flowOn(Dispatchers.Default)
        }
        stdoutFlow.takeWhile { it != null }.map {
            require(it != null)
            stdout.write(it)
        }.flowOn(Dispatchers.IO).collect()
        cancel()
    }
}

private inline fun Stmt.use(block: (BufferedReader, BufferedWriter, BufferedWriter) -> Unit) {
    (stdin?.bufferedReader() ?: BufferedReader(System.`in`.reader())).useIf(stdin != null) { stdin ->
        (stdout?.bufferedWriter() ?: BufferedWriter(System.out.writer())).useIf(stdout != null) { stdout ->
            (stderr?.bufferedWriter() ?: BufferedWriter(System.err.writer())).useIf(stderr != null) { stderr ->
                block(stdin, stdout, stderr)
            }
        }
    }
}

private inline fun <T : Closeable> T.useIf(cond: Boolean, block: (T) -> Unit) {
    if (cond) use(block)
    else block(this)
}
