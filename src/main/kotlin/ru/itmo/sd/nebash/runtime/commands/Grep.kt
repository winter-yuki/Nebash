package ru.itmo.sd.nebash.runtime.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoSuchOption
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import ru.itmo.sd.nebash.Env
import ru.itmo.sd.nebash.runtime.*
import ru.itmo.sd.nebash.utils.collectWhileNotNull
import ru.itmo.sd.nebash.utils.getString
import java.util.regex.PatternSyntaxException
import kotlin.io.path.Path
import kotlin.io.path.forEachLine

abstract class GrepException(name: String, e: Throwable? = null) : NebashRuntimeException(name, e)

class GrepRegexParamExpectedException : GrepException("grep: regex param expected")

class GrepNoSuchOptionException(msg: String, e: Throwable) : GrepException(msg, e)

class GrepPatternSyntaxException(msg: String, e: Throwable) : GrepException("Regex: $msg", e)

class GrepBadParameterValueException(msg: String, e: Throwable) : GrepException(msg, e)

object Grep : Command {
    override fun invoke(env: Env, args: List<CommandArg>, stdin: Stdin, stderr: Stderr): Stdout = flow {
        val cli = try {
            Cli().apply { parse(args.map { it.arg }) }
        } catch (e: NoSuchOption) {
            throw GrepNoSuchOptionException(e.message.orEmpty(), e)
        } catch (e: BadParameterValue) {
            throw GrepBadParameterValueException(e.message.orEmpty(), e)
        }
        if (cli.help) {
            emit(cli.getFormattedHelp())
            emit("\n")
            return@flow
        }
        val regex = cli.getRegex()
        if (cli.files.isEmpty()) {
            Processor(cli.nAfterMatch, regex).run {
                stdin.collectWhileNotNull { line ->
                    processLine(line)
                }
            }
        } else {
            cli.files.forEach { name ->
                Processor(cli.nAfterMatch, regex).run {
                    Path(name).forEachLine { line ->
                        processLine(line, end = "\n")
                    }
                }
            }
        }
    }

    private fun Cli.getRegex(): Regex {
        val string = regex ?: throw GrepRegexParamExpectedException()
        val worded = string.takeUnless { wordRegex } ?: "\\b$string\\b"
        val options = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else setOf()
        return try {
            worded.toRegex(options)
        } catch (e: PatternSyntaxException) {
            throw GrepPatternSyntaxException(e.message.orEmpty(), e)
        }
    }

    private class Processor(val nAfter: Int, val regex: Regex) {
        private var rest = nAfter

        suspend fun FlowCollector<String>.processLine(line: String, end: String = "") {
            if (regex.containsMatchIn(line)) {
                emit(line)
                emit(end)
                rest = nAfter
            } else if (rest > 0) {
                emit(line)
                emit(end)
                rest--
            }
        }
    }
}

private class Cli : CliktCommand() {
    val ignoreCase: Boolean by option(
        "-i", "--ignore-case",
        help = ignoreHelp
    ).flag()
    val wordRegex: Boolean by option(
        "-w", "--word-regexp",
        help = wordHelp
    ).flag()
    val help: Boolean by option("-h", "--help").flag()
    val nAfterMatch: Int by option(
        "-A", "--after-context",
        help = afterHelp
    ).int().default(0)
    val regex: String? by argument().optional()
    val files: List<String> by argument().multiple()

    override fun run() = Unit

    companion object {
        private val ignoreHelp by lazy {
            msg("ignoreCase.txt")
        }
        private val wordHelp by lazy {
            msg("wordRegex.txt")
        }
        private val afterHelp by lazy {
            msg("nAfterMatch.txt")
        }

        private fun msg(name: String) = getString("grep", name)
    }
}
