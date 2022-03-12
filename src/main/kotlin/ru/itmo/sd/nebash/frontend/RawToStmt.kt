package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.*
import ru.itmo.sd.nebash.backend.cn
import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.raw.rs

fun RawStmt.toStmt(state: State): Stmt {
    val (assignments, tail) = stmt.trim().rs.parseAssignments(state)
    println("ass = $assignments")
    println("tail = $tail")
    return pwd
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

private val pwd = PipelineStmt(
    pipeline = listOf(PipelineAtom("pwd".cn)),
)

private val stmt = PipelineStmt(
    localAssignments = listOf(Assignment("a".vn, "1".vv)),
    pipeline = listOf(
        PipelineAtom("cat".cn),
        PipelineAtom("wc".cn),
        PipelineAtom("pwd".cn)
    ),
)
