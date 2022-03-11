package ru.itmo.sd.nebash.backend

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.MutableEnv
import ru.itmo.sd.nebash.Stmt
import java.io.BufferedReader
import java.io.BufferedWriter

fun Stmt.execute(env: MutableEnv) {
    return // TODO
    require(assignmentList.isNotEmpty() || pipeline.isNotEmpty()) { "Statement should not be empty " }
    // Files should be created also if no pipeline provided
    val stdin = if (stdin == null) BufferedReader(System.`in`.reader()) else TODO("Open stdin file")
    val stdout = if (stdout == null) BufferedWriter(System.out.writer()) else TODO("Open stdout file")
    val stderr = if (stderr == null) BufferedWriter(System.err.writer()) else TODO("Open stderr file")
    if (pipeline.isEmpty()) {
        assignmentList.forEach { env[it.name] = it.value }
    } else {
        val newEnv = MutableEnv(env).apply {
            assignmentList.forEach {
                set(it.name, it.value)
                export(it.name)
            }
        }
        execute(newEnv, stdin, stdout, stderr)
    }
}

private fun Stmt.execute(
    env: Env, stdin: BufferedReader, stdout: BufferedWriter, stderr: BufferedWriter
) = runBlocking {
    var stdinFlow: Flow<String> = channelFlow { stdin.readLine() ?: cancel() }
    val stderrFlow = MutableSharedFlow<String>()
    launch { stderrFlow.collect { stderr.write(it) } }
//    pipeline.drop(1).forEach {
//        val cmd = Command(it.name)
//        cmd(it.args, stdinFlow, ) }
}

//    {
//    TODO()
//}

//    fun execute(command: RawStmt) = runBlocking {
//        val stdinFlow: Flow<String> = channelFlow { readlnOrNull() ?: cancel() }
//        val stderrFlow = MutableSharedFlow<String>()
//        launch {
//            stderrFlow.collect { println(it) }
//        }
//        execute(command, stdinFlow, stderrFlow).collect {
//            println(it)
//        }
//    }
//
//    private fun execute(
//        command: RawStmt, stdin: Flow<String>,
//        stderr: MutableSharedFlow<String>
//    ): Flow<String> = channelFlow {
//        stderr.emit("Err")
//        send("Out")
//        cancel()
//    }
