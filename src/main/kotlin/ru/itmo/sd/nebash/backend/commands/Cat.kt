package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.*
import ru.itmo.sd.nebash.utils.collectWhileNotNull
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Unix-like utility that prints stdin to stdout.
 */
object Cat : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = flow {
        if (args.isEmpty()) stdin.collectWhileNotNull { emit(it) }
        else args.forEach { arg -> emit(Path(arg.arg).readText()) }
    }
}
