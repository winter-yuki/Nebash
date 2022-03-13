package ru.itmo.sd.nebash.frontend.pipeline

import ru.itmo.sd.nebash.utils.unreachable

fun String.tokenize(): List<Token> {
    val builder = TokensBuilder()
    forEach { c -> builder.add(c) }
    return builder.build()
}

private enum class Quote(val repr: Char) {
    Single('\''), Double('\"')
}

private class TokensBuilder {
    private val tokens = mutableListOf<Token>()
    private val builder = StringBuilder()
    private var quote: Quote? = null

    fun add(c: Char) {
        if (quote == null) onNullQuote(c) else {
            if (quote?.repr != c) builder.append(c)
            else onClosingQuote(c)
        }
    }

    fun build(): List<Token> {
        if (quote != null) throw ClosingQuoteExpected(quote!!.repr)
        flush { Part(it) }
        return tokens
    }

    private fun onNullQuote(c: Char) {
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
                tokens += Pipe
            }
            else -> builder.append(c)
        }
    }

    private fun onClosingQuote(c: Char) {
        quote = null
        when (c) {
            Quote.Single.repr -> flush { SingleQuoted(it) }
            Quote.Double.repr -> flush { DoubleQuoted(it) }
            else -> unreachable
        }
    }

    private fun flush(tokenBuilder: (String) -> Token) {
        if (builder.isNotBlank()) {
            tokens += tokenBuilder(builder.toString())
            builder.clear()
        }
    }
}
