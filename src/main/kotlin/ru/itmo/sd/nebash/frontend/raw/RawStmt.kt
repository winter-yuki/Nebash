package ru.itmo.sd.nebash.frontend.raw

@JvmInline
value class RawStmt(val stmt: String)

val String.rs: RawStmt
    get() = RawStmt(this)

val RawStmt.isExit: Boolean
    get() = stmt.trim() == "exit"
