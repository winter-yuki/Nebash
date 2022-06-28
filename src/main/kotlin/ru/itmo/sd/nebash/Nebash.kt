package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.toStmt
import ru.itmo.sd.nebash.runtime.execute

/**
 * Represents Nebash interpreter.
 */
class Nebash(private val state: MutableState = MutableState()) {
    fun execute(rawStmt: RawStmt) = rawStmt.toStmt(state).execute(state)
}

/**
 * Base class for all [Nebash] exceptions.
 */
abstract class NebashException(message: String, e: Throwable? = null) : RuntimeException(message, e)
