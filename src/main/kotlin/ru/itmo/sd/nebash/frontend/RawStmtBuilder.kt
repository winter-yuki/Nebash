package ru.itmo.sd.nebash.frontend

/**
 * Builds Nebash command line by line.
 */
class RawStmtBuilder {

    private val builder = StringBuilder()
    private var mark: NextPartMark? = null

    /**
     * Append statement line.
     */
    fun append(stmtLine: String) {
        updateMark(stmtLine)
        builder.append(
            if (mark != NextPartMark.Backslash) stmtLine
            else stmtLine.dropLast(2) // Remove \\\n
        )
    }

    fun isEmpty(): Boolean = builder.isEmpty()

    /**
     * Build statement if it is complete.
     */
    fun buildOrNull(): RawStmt? =
        if (mark != null) null
        else RawStmt(builder.toString())

    private fun updateMark(stmt: String) {
        if (mark == NextPartMark.Backslash) {
            mark = null
        }

        val marks = stmt.mapNotNull {
            when (it) {
                NextPartMark.SingleQuote.symbol -> NextPartMark.SingleQuote
                NextPartMark.Quote.symbol -> NextPartMark.Quote
                else -> null
            }
        }
        marks.forEach { nextMark ->
            mark = when (mark) {
                null -> nextMark
                nextMark -> null
                else -> mark
            }
        }

        if (mark == null && stmt.endsWith(NextPartMark.Backslash.symbol + "\n")) {
            mark = NextPartMark.Backslash
        }
    }
}

/**
 * Markers that the last statement part was not least.
 */
private enum class NextPartMark(val symbol: Char) {
    SingleQuote('\''), Quote('\"'), Backslash('\\')
}
