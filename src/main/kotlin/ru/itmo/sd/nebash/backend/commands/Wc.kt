package ru.itmo.sd.nebash.backend.commands

import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.backend.*
import ru.itmo.sd.nebash.utils.collectWhileNotNull
import kotlin.io.path.Path
import kotlin.io.path.forEachLine

/**
 * Word count unix-like utility: takes files and count lines, words and chars in them.
 */
object Wc : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = flow {
        var nWords = 0
        var nLines = 0
        var nChars = 0

        fun String.process() {
            nWords += split("""\s+""".toRegex()).size
            nLines += count { it == '\n' }
            nChars += length
        }

        fun prettyPrint(filename: String? = null): String {
            val res = "\t$nLines\t$nWords\t$nChars" +
                    if (filename == null) "\n" else "\t$filename\n"
            nWords = 0
            nLines = 0
            nChars = 0
            return res
        }

        if (args.isEmpty()) {
            stdin.collectWhileNotNull { it.process() }
            emit(prettyPrint())
            return@flow
        }
        args.forEach {
            Path(it.arg).forEachLine { line -> line.process() }
            emit(prettyPrint(it.arg))
        }
    }
}
