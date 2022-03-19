package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.frontend.FrontendException
import ru.itmo.sd.nebash.frontend.raw.BuildResult
import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.raw.RawStmtBuilder
import ru.itmo.sd.nebash.runtime.NebashRuntimeException

fun main() {
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
        } catch (e: NebashRuntimeException) {
            println("Nebash execution error: ${e.message}")
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
        builder.append(line)
        return when (val res = builder.build()) {
            is BuildResult.Stmt -> ReadStmt.Ok(res.stmt)
            is BuildResult.Empty -> ReadStmt.Empty
            is BuildResult.NotFinished -> {
                print(continuePrompt)
                continue
            }
        }
    }
}

private val RawStmt.isExit: Boolean
    get() = stmt.trim() == "exit"
