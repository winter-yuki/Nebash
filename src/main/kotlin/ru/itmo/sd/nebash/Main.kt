package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.backend.BackendException
import ru.itmo.sd.nebash.frontend.FrontendException
import ru.itmo.sd.nebash.frontend.RawStmt
import ru.itmo.sd.nebash.frontend.RawStmtBuilder
import ru.itmo.sd.nebash.frontend.isExit

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
        } catch (e: FrontendException) {
            println("Nebash parse error: ${e.message}")
        } catch (e: BackendException) {
            println("Nebash execution error: ${e.message}")
        } catch (e: Exception) {
            println("Internal error: $e")
            e.printStackTrace()
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
        val line = readlnOrNull()?.plus('\n') ?: return ReadStmt.End
        if (line.isBlank() && builder.isEmpty()) return ReadStmt.Empty
        builder.append(line)
        val stmt = builder.buildOrNull()
        if (stmt == null) {
            print(continuePrompt)
            continue
        }
        return ReadStmt.Ok(stmt)
    }
}
