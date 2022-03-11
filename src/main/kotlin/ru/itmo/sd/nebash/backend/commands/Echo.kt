package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.Command
import ru.itmo.sd.nebash.backend.CommandArg

object Echo : Command {
    override fun invoke(
        env: Env, args: List<CommandArg>,
        stdin: Flow<String?>, stderr: MutableSharedFlow<String>
    ): Flow<String> = flow {
        emit(args.joinToString(" ") { it.arg })
    }
}