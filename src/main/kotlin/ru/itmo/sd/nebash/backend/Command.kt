package ru.itmo.sd.nebash.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.itmo.sd.nebash.Env

interface Command {
    operator fun invoke(
        env: Env, args: List<CommandArg>,
        stdin: Flow<String?>, stderr: MutableSharedFlow<String>
    ): Flow<String>
}

@JvmInline
value class CommandName(val name: String)

val String.cn: CommandName
    get() = CommandName(this)

@JvmInline
value class CommandArg(val arg: String)

val String.ca: CommandArg
    get() = CommandArg(this)
