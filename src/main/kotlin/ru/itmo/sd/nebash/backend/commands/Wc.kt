package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.Command
import ru.itmo.sd.nebash.backend.CommandArg
import ru.itmo.sd.nebash.backend.collectWhileNotNull

object Wc : Command {
    override fun invoke(
        env: Env, args: List<CommandArg>,
        stdin: Flow<String?>, stderr: MutableSharedFlow<String>
    ): Flow<String> = flow {
        var nWords = 0
        var nLines = 0
        var nChars = 0
        stdin.collectWhileNotNull { s ->
            nWords += s.split("""\s+""").size
            nLines += s.count { it == '\n' }
            nChars += s.length
        }
        emit("\t$nLines\t$nWords\t$nChars\n")
    }
}
