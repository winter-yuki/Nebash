package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.*
import ru.itmo.sd.nebash.frontend.raw.RawStmt

data class RawAssignmentStmt(
    val export: Boolean = false,
    val list: List<Assignment> = listOf()
)

fun RawStmt.parseAssignments(state: State): Pair<RawAssignmentStmt, String> {
    var export = false
    val list = mutableListOf<Assignment>()
    var tail = stmt
    while (true) {
        val match = assignmentsRegex.matchEntire(tail.trimStart())
            ?: return RawAssignmentStmt(export, list) to tail
        val (_, newExport, name, value, newTail) = match.groupValues
        export = export || newExport.isNotEmpty()
        list += Assignment(name.vn, value.parseValue(state))
        tail = newTail
    }
}

private val nameRegex = """[_a-zA-Z][_a-zA-Z0-9]*""".toRegex()
private val valueRegex = """[^\s'"]*|'[^']*?'|"[^"]*?"""".toRegex()
private val assignmentRegex = """($nameRegex)=($valueRegex)""".toRegex()
private val assignmentsRegex = """(export\s+)*$assignmentRegex($|\s[\s\S]*)""".toRegex()

fun String.parseValue(state: State): VarValue {
    if (startsWith('\'')) return substring(1, lastIndex).vv
    val s = if (!startsWith('\"')) this else substring(1, lastIndex)
    return buildString {
        val parts = s.split('$')
        append(parts.first())
        parts.drop(1).forEach { part ->
            val split = part.split("""\s""".toRegex(), limit = 2)
            append(state[split.first().vn] ?: "")
            append(split.getOrElse(1) { "" })
        }
    }.vv
}
