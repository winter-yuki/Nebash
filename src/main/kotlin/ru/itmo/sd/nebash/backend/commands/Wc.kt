package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.*
import ru.itmo.sd.nebash.utils.collectWhileNotNull
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Word count unix-like utility: takes files and count lines, words and chars in them.
 */
object Wc : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = flow {
        var nWords = 0
        var nLines = 0
        var nChars = 0

        fun String.process() {
            nWords += split("""\s+""").size
            nLines += count { it == '\n' }
            nChars += length
        }

        if (args.isEmpty()) stdin.collectWhileNotNull { it.process() }
        else args.forEach { Path(it.arg).readText().process() }
        emit("\t$nLines\t$nWords\t$nChars\n")
    }
}
