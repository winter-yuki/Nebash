package ru.itmo.sd.nebash.frontend.raw

import ru.itmo.sd.nebash.utils.indexOfLastOrNull

/**
 * Builds Nebash command line by line.
 */
class RawStmtBuilder {

    private val builder = StringBuilder()
    private var mark: NextPartMark? = null

    /**
     * Append statement part.
     */
    fun append(stmtPart: String) {
        builder.append(stmtPart)
        updateMark(stmtPart)
    }

    /**
     * Build statement if it is complete.
     */
    fun buildOrNull(): RawStmt? =
        if (mark != null) null
        else RawStmt(builder.toString())

    fun isEmpty(): Boolean = builder.isEmpty()

    private fun updateMark(stmt: String) {
        val singleQuote = stmt.indexOfLastOrNull { it == NextPartMark.SingleQuote.symbol }
        val quote = stmt.indexOfLastOrNull { it == NextPartMark.Quote.symbol }
        val backslash = stmt.endsWith(NextPartMark.Backslash.symbol)
        updateMark(singleQuote, quote, backslash)
    }

    private fun updateMark(singleQuote: Int?, quote: Int?, backslash: Boolean) {
        val mark = mark
        if (mark == null) setMark(singleQuote, quote, backslash)
        else updateMark(mark, singleQuote, quote, backslash)
    }

    private fun setMark(singleQuote: Int?, quote: Int?, backslash: Boolean) {
        mark = when {
            singleQuote == null && quote == null && !backslash -> null
            singleQuote == null && quote == null && backslash -> NextPartMark.Backslash
            singleQuote == null -> NextPartMark.Quote
            quote == null -> NextPartMark.SingleQuote
            else -> if (singleQuote < quote) NextPartMark.SingleQuote else NextPartMark.Quote
        }
    }

    private fun updateMark(mark: NextPartMark, singleQuote: Int?, quote: Int?, backslash: Boolean) {
        when (mark) {
            NextPartMark.SingleQuote -> singleQuote?.let {
                setMark(singleQuote.truncate(it), quote.truncate(it), backslash)
            }
            NextPartMark.Quote -> quote?.let {
                setMark(singleQuote.truncate(it), quote.truncate(it), backslash)
            }
            NextPartMark.Backslash -> setMark(singleQuote, quote, backslash)
        }
    }

    private fun Int?.truncate(lowerBound: Int): Int? = if (this == null) null else {
        if (this <= lowerBound) null else this
    }
}

/**
 * Markers that the last statement part was not least.
 */
private enum class NextPartMark(val symbol: Char) {
    SingleQuote('\''), Quote('\"'), Backslash('\\')
}
