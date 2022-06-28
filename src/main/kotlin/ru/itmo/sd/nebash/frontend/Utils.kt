package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.toVn

/**
 * Stupidly substitute values into dollar positions.
 */
fun String.substitute(state: State): String = buildString {
    if (this@substitute.isEmpty()) return@buildString
    val parts = this@substitute.split('$')
    append(parts.first())
    parts.asSequence().drop(1).forEach { part ->
        val split = part.split("""\s""".toRegex(), limit = 2)
        val name = split.first()
        append(if (name.isBlank()) "\$" else state[name.toVn()] ?: "")
        append(part.getOrNull(name.length) ?: "")
        append(split.getOrElse(1) { "" })
    }
}
