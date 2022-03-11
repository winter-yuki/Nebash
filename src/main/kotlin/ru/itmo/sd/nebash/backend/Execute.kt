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

/**
 * Execute Nebash [Stmt].
 */
fun Stmt.execute(env: MutableEnv) {
    val stdin = if (stdin == null) BufferedReader(System.`in`.reader()) else TODO("Open stdin file")
    val stdout = if (stdout == null) BufferedWriter(System.out.writer()) else TODO("Open stdout file")
    val stderr = if (stderr == null) BufferedWriter(System.err.writer()) else TODO("Open stderr file")
    try {
        when (this) {
            is Assignments -> eval(env)
            is Pipeline -> eval(env, stdin, stdout, stderr)
        }
    } finally {
        stdout.flush()
        stderr.flush()
    }
}

private fun Assignments.eval(env: MutableEnv) {
    list.forEach { (name, value) ->
        env[name] = value
        if (export) env.export(name)
    }
}

private fun Pipeline.eval(
    env: Env, stdin: BufferedReader, stdout: BufferedWriter, stderr: BufferedWriter
): Unit = runBlocking {
    val newEnv = MutableEnv(env).apply {
        localAssignments.forEach { (name, value) ->
            set(name, value)
            export(name)
        }
    }
    val stdinFlow: Flow<String?> = flow {
        while (true) {
            val input = stdin.readLine() ?: break
            if (input == "end") break
            emit(input + '\n')
        }
        emit(null)
    }.flowOn(Dispatchers.IO)
    val stderrFlow = MutableSharedFlow<String>()
    launch {
        launch { stderrFlow.map { stderr.write(it); stderr.flush() }.flowOn(Dispatchers.IO).collect() }
        pipeline.fold(stdinFlow) { inFlow, (name, args) ->
            commandByName(name)(newEnv, args, inFlow, stderrFlow).flowOn(Dispatchers.Default)
        }.takeWhile { it != null }.map {
            require(it != null)
            stdout.write(it); stdout.flush()
        }.flowOn(Dispatchers.IO).collect()
        cancel()
    }
}
