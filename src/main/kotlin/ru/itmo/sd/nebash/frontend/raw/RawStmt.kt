package ru.itmo.sd.nebash.frontend.raw

/**
 * Represents raw statement user enters.
 */
@JvmInline
value class RawStmt(val stmt: String) {
    init {
        require(stmt.isNotBlank())
    }
}

fun String.toRs() = RawStmt(this)
