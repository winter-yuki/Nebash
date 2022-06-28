package ru.itmo.sd.nebash.runtime.commands

import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.runtime.*

/**
 * Unix-like command that prints provided arguments.
 */
object Echo : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = flow {
        emit(args.joinToString(" ", postfix = "\n") { it.arg })
    }
}
