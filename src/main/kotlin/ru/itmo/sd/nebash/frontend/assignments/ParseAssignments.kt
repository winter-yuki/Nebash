package ru.itmo.sd.nebash.frontend.assignments

import ru.itmo.sd.nebash.*
import ru.itmo.sd.nebash.frontend.substitute

/**
 * Assignment parse result.
 */
data class RawAssignmentStmt(
    val export: Boolean = false,
    val list: List<Assignment> = listOf()
)

/**
 * Parse assignment list.
 * @return Parsing result and tail.
 */
fun String.parseAssignments(state: State): Pair<RawAssignmentStmt, String> {
    var export = false
    val list = mutableListOf<Assignment>()
    var tail = this
    while (true) {
        val match = assignmentsRegex.matchEntire(tail)
            ?: return RawAssignmentStmt(export, list) to tail
        val (_, newExport, name, value, newTail) = match.groupValues
        export = export || newExport.isNotEmpty()
        list += Assignment(name.toVn(), value.parseValue(state))
        tail = newTail
    }
}

private val nameRegex = """[_a-zA-Z][_a-zA-Z0-9]*""".toRegex()
private val valueRegex = """[^\s'"]*|'[^']*?'|"[^"]*?"""".toRegex()
private val assignmentRegex = """($nameRegex)=($valueRegex)""".toRegex()
private val assignmentsRegex = """\s*(export\s+)*$assignmentRegex(?:$|\s+([\s\S]*))""".toRegex()

private fun String.parseValue(state: State): VarValue =
    if (startsWith('\'')) substring(1, lastIndex).vv else {
        val s = if (startsWith('\"')) substring(1, lastIndex) else this
        s.substitute(state).vv
    }
