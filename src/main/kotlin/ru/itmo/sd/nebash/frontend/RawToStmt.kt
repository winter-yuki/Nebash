package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.AssignmentStmt
import ru.itmo.sd.nebash.PipelineStmt
import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.Stmt
import ru.itmo.sd.nebash.frontend.assignments.parseAssignments
import ru.itmo.sd.nebash.frontend.pipeline.parsePipeline
import ru.itmo.sd.nebash.frontend.raw.RawStmt

/**
 * Parse raw statement.
 */
fun RawStmt.toStmt(state: State): Stmt {
    val (assignments, tail) = stmt.trim().parseAssignments(state)
    val pipeline = tail.parsePipeline(state)
    require(assignments.list.isNotEmpty() || pipeline.isNotEmpty()) { "Parsing error" }
    return if (assignments.export || pipeline.isEmpty()) {
        AssignmentStmt(
            export = assignments.export,
            assignments = assignments.list
        )
    } else {
        PipelineStmt(
            localAssignments = assignments.list,
            pipeline = pipeline
        )
    }
}
