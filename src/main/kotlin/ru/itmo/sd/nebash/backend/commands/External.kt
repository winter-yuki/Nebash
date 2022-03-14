package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.*

/**
 * Run external process with specified name.
 */
class External(private val name: CommandName) : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = channelFlow<String> {
        val builder = ProcessBuilder(name.name).apply {
            val e = environment()
            env.forEach { (name, value) ->
                e[name.name] = value.value
            }
            command().addAll(args.map { it.arg })
        }
        val process = withContext(Dispatchers.IO) {
            try {
                builder.start()
            } catch (e: RuntimeException) {
                throw FailToStartExternalProcess(e)
            }
        }
        coroutineScope {
            launch(Dispatchers.IO) {
                process.errorStream.bufferedReader().useLines {
                    it.forEach { line ->
                        stderr.emit(line + '\n')
                    }
                }
            }
            launch(Dispatchers.IO) {
                process.inputStream.bufferedReader().useLines {
                    it.forEach { line ->
                        send(line + '\n')
                    }
                }
            }
            launch {
                process.outputStream.bufferedWriter().use { out ->
                    stdin
                        .takeWhile { it != null }
                        .map { require(it != null); out.write(it) }
                        .flowOn(Dispatchers.IO).collect()
                }
            }
        }
        val exitCode = process.waitFor()
        if (exitCode != 0) throw NonZeroExternalProcessExitCode(exitCode)
    }.flowOn(Dispatchers.IO)
}
