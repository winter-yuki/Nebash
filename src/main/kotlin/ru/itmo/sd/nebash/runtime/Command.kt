package ru.itmo.sd.nebash.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.itmo.sd.nebash.Env

/**
 * Represents Nebash command that can be invoked.
 */
interface Command {
    operator fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout
}

typealias Stdin = Flow<String?>
typealias Stderr = MutableSharedFlow<String>
typealias Stdout = Flow<String>

@JvmInline
value class CommandName(val name: String) {
    init {
        require(name.isNotBlank())
    }
}

fun String.toCn() = CommandName(this)

@JvmInline
value class CommandArg(val arg: String)

val String.ca: CommandArg
    get() = CommandArg(this)
