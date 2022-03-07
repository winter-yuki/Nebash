package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.backend.CommandArg
import ru.itmo.sd.nebash.backend.CommandName
import ru.itmo.sd.nebash.utils.Filename

class Assignment(
    val name: VarName,
    val value: VarValue
)

class PipelineAtom(
    val name: CommandName,
    val args: List<CommandArg>
)

class Stmt(
    val assignmentList: List<Assignment>,
    val pipeline: List<PipelineAtom>,
    val stdin: Filename?,
    val stdout: Filename?,
    val stderr: Filename?
)
