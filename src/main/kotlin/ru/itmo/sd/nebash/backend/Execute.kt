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
import java.io.IOException

/**
 * Execute Nebash [Stmt].
 */
fun Stmt.execute(state: MutableState) {
    val stdin = if (stdin == null) BufferedReader(System.`in`.reader()) else TODO("Open stdin file")
    val stdout = if (stdout == null) BufferedWriter(System.out.writer()) else TODO("Open stdout file")
    val stderr = if (stderr == null) BufferedWriter(System.err.writer()) else TODO("Open stderr file")
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
    val newState = MutableState(state).apply {
        localAssignments.forEach { (name, value) ->
            set(name, value)
            export(name)
        }
    }
    val stdinFlow: Stdin = flow {
        while (true) {
            val input = stdin.readLine() ?: break
            val eof = "end"
            if (input == eof) {
                emit(input + '\n')
                break
            }
            if (input.endsWith(eof)) {
                emit(input.dropLast(eof.length))
                break
            }
            emit(input + '\n')
        }
        emit(null)
    }.flowOn(Dispatchers.IO)

    val stderrFlow = MutableSharedFlow<String>()

    launch {
        launch {
            stderrFlow.map { stderr.write(it) }.flowOn(Dispatchers.IO).collect()
        }
        val stdoutFlow = pipeline.fold(stdinFlow) { inFlow, (name, args) ->
            val cmd = commandByName(name)
            val outFlow = cmd(newState.env, args, inFlow, stderrFlow)
            outFlow.flowOn(Dispatchers.Default)
        }
        stdoutFlow.takeWhile { it != null }.map {
            require(it != null)
            stdout.write(it)
        }.flowOn(Dispatchers.IO).collect()
        cancel()
    }
}
