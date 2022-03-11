package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.Command
import ru.itmo.sd.nebash.backend.CommandArg
import ru.itmo.sd.nebash.backend.CommandName

class External(private val name: CommandName) : Command {
    override fun invoke(
        env: Env, args: List<CommandArg>,
        stdin: Flow<String?>, stderr: MutableSharedFlow<String>
    ): Flow<String> = flow {
        val builder = ProcessBuilder(name.name, *args.map { it.arg }.toTypedArray()).apply {
            val e = environment()
            env.exported.forEach { (name, value) ->
                e[name.name] = value.value
            }
        }
        val process = withContext(Dispatchers.IO) {
            builder.start()
        }
        coroutineScope {
            launch(Dispatchers.IO) {
                val err = process.errorStream.bufferedReader()
                while (true) {
                    val line = err.readLine() ?: break
                    stderr.emit(line + '\n')
                }
            }
            val out = process.outputStream.bufferedWriter()
            launch {
                stdin
                    .takeWhile { it != null }
                    .map { require(it != null); out.write(it) }
                    .flowOn(Dispatchers.IO).collect()
            }
        }
        withContext(Dispatchers.IO) {
            val `in` = process.inputStream.bufferedReader()
            while (true) {
                val line = `in`.readLine() ?: break
                emit(line + '\n')
            }
        }
    }
}
