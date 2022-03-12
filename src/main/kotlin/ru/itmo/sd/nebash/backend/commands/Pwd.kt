package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.*

/**
 * Print working directory unix-like command.
 */
object Pwd : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = flow {
        emit(System.getProperty("user.dir") + '\n')
    }
}
