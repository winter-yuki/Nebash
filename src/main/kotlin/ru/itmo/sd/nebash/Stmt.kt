package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.backend.CommandArg
import ru.itmo.sd.nebash.backend.CommandName
import ru.itmo.sd.nebash.utils.Filename

sealed interface Stmt {
    val stdin: Filename?
    val stdout: Filename?
    val stderr: Filename?
}

data class Assignments(
    val export: Boolean,
    val list: List<Assignment>,
    override val stdin: Filename?,
    override val stdout: Filename?,
    override val stderr: Filename?
) : Stmt {
    init {
        require(list.isNotEmpty())
    }
}

data class Pipeline(
    val localAssignments: List<Assignment>,
    val pipeline: List<PipelineAtom>,
    override val stdin: Filename?,
    override val stdout: Filename?,
    override val stderr: Filename?
) : Stmt {
    init {
        require(pipeline.isNotEmpty())
    }
}

data class Assignment(
    val name: VarName,
    val value: VarValue
)

data class PipelineAtom(
    val name: CommandName,
    val args: List<CommandArg>
)
