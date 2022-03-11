package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.backend.execute
import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.toStmt

class Nebash(private val env: MutableEnv = MutableEnv()) {
    fun execute(stmt: RawStmt) = stmt.toStmt(env).execute(env)
}

/**
 * Base class for all [Nebash] exceptions.
 */
abstract class NebashException(message: String) : RuntimeException(message)
