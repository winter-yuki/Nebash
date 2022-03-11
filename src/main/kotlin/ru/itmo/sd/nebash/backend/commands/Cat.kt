package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.Command
import ru.itmo.sd.nebash.backend.CommandArg
import ru.itmo.sd.nebash.backend.collectWhileNotNull
import kotlin.io.path.Path
import kotlin.io.path.readText

object Cat : Command {
    override fun invoke(
        env: Env, args: List<CommandArg>,
        stdin: Flow<String?>, stderr: MutableSharedFlow<String>
    ): Flow<String> =
        if (args.isEmpty()) flow { stdin.collectWhileNotNull { emit(it) } }
        else flow { args.forEach { arg -> emit(Path(arg.arg).readText()) } }
}
