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

object Wc : Command {
    override fun invoke(
        env: Env, args: List<CommandArg>,
        stdin: Flow<String?>, stderr: MutableSharedFlow<String>
    ): Flow<String> {
        var nWords = 0
        var nLines = 0
        var nChars = 0

        fun String.process() {
            nWords += split("""\s+""").size
            nLines += count { it == '\n' }
            nChars += length
        }

        return flow {
            if (args.isEmpty()) stdin.collectWhileNotNull { it.process() }
            else args.forEach { Path(it.arg).readText().process() }
            emit("\t$nLines\t$nWords\t$nChars\n")
        }
    }
}
