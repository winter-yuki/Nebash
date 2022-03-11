package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.raw.RawStmtBuilder
import ru.itmo.sd.nebash.frontend.raw.isExit

private fun main() {
    val interpreter = Nebash()
    while (true) {
        try {
            val stmt = when (val res = readStmt()) {
                is ReadStmt.Ok -> res.stmt
                is ReadStmt.Empty -> continue
                is ReadStmt.End -> break
            }
            if (stmt.isExit) {
                println("exit")
                return
            }
            interpreter.execute(stmt)
        } catch (e: NebashException) {
            println("Error: ${e.message}")
        }
    }
}

private sealed interface ReadStmt {
    class Ok(val stmt: RawStmt) : ReadStmt
    object Empty : ReadStmt
    object End : ReadStmt
}

private fun readStmt(prompt: String = "$ ", continuePrompt: String = "> "): ReadStmt {
    print(prompt)
    val builder = RawStmtBuilder()
    while (true) {
        val line = readlnOrNull() ?: return ReadStmt.End
        if (line.isBlank()) return ReadStmt.Empty
        builder.append(line)
        val stmt = builder.buildOrNull()
        if (stmt == null) {
            print(continuePrompt)
            continue
        }
        return ReadStmt.Ok(stmt)
    }
}
