package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.vn

/**
 * Substitute values into dollar positions.
 */
fun String.substitute(state: State): String = buildString {
    val parts = this@substitute.split('$')
    append(parts.first())
    parts.drop(1).forEach { part ->
        val split = part.split("""\s""".toRegex(), limit = 2)
        append(state[split.first().vn] ?: "")
        append(split.getOrElse(1) { "" })
    }
}
