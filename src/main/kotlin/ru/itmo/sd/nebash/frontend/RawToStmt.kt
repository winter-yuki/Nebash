package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.*
import ru.itmo.sd.nebash.backend.cn
import ru.itmo.sd.nebash.frontend.raw.RawStmt

// TODO
fun RawStmt.toStmt(env: Env): Stmt =
    Stmt(
        assignmentList = listOf(Assignment("a".vn, "1".vv)),
        pipeline = listOf(PipelineAtom("pwd".cn, listOf())),
        stdin = null,
        stdout = null,
        stderr = null
    )
