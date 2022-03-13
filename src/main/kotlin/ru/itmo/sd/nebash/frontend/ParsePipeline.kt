package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.PipelineAtom
import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.backend.ca
import ru.itmo.sd.nebash.backend.cn
import ru.itmo.sd.nebash.utils.split
import ru.itmo.sd.nebash.utils.unreachable

class EmptyPipelineAtomException : FrontendException("command between pipes can not be empty")

class ClosingQuoteExpected(quote: Char) : FrontendException("closing quote $quote missed")

fun String.parsePipeline(state: State): List<PipelineAtom> {
    val tokens = tokenize().map { it.substitute(state) }
    val atoms = tokens.split({ it is Pipe }) { token ->
        when (token) {
            is Part -> token.s.trim().split("""\s""".toRegex())
            is SingleQuoted -> listOf(token.s)
            is DoubleQuoted -> listOf(token.s)
            is Pipe -> error("Pipes should have been split off")
        }
    }
    return atoms.map { atom ->
        PipelineAtom(
            name = atom.firstOrNull()?.cn ?: throw EmptyPipelineAtomException(),
            args = atom.drop(1).map { it.ca }
        )
    }
}

private sealed interface Token {
    fun substitute(state: State): Token
}

private class Part(val s: String) : Token {
    override fun substitute(state: State): Token = Part(s.substitute(state))
}

private class SingleQuoted(val s: String) : Token {
    override fun substitute(state: State): Token = this
}

private class DoubleQuoted(val s: String) : Token {
    override fun substitute(state: State): Token = DoubleQuoted(s.substitute(state))
}

private object Pipe : Token {
    override fun substitute(state: State): Token = Pipe
}

private enum class Quote(val repr: Char) {
    Single('\''), Double('\"')
}

private fun String.tokenize(): List<Token> {
    val res = mutableListOf<Token>()
    val builder = StringBuilder()
    fun flush(tokenBuilder: (String) -> Token) {
        if (builder.isNotBlank()) {
            res += tokenBuilder(builder.toString())
            builder.clear()
        }
    }

    var quote: Quote? = null
    forEach { c ->
        if (quote == null) {
            when (c) {
                Quote.Single.repr -> {
                    quote = Quote.Single
                    flush { Part(it) }
                }
                Quote.Double.repr -> {
                    quote = Quote.Double
                    flush { Part(it) }
                }
                '|' -> {
                    flush { Part(it) }
                    res += Pipe
                }
                else -> builder.append(c)
            }
        } else {
            if (quote?.repr != c) builder.append(c) else {
                quote = null
                when (c) {
                    Quote.Single.repr -> flush { SingleQuoted(it) }
                    Quote.Double.repr -> flush { DoubleQuoted(it) }
                    else -> unreachable
                }
            }
        }
    }
    if (quote != null) throw ClosingQuoteExpected(quote!!.repr)
    flush { Part(it) }
    return res
}
