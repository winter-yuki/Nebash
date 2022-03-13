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

val String.rs: RawStmt
    get() = RawStmt(this)

val RawStmt.isExit: Boolean
    get() = stmt.trim() == "exit"
