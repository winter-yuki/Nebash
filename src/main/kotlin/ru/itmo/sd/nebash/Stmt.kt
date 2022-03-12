package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.backend.CommandArg
import ru.itmo.sd.nebash.backend.CommandName
import ru.itmo.sd.nebash.utils.Filename

/**
 * Represents [Nebash] statement.
 * @property stdin Standard input redirection, default if null.
 * @property stdout Standard output redirection, default if null.
 * @property stderr Standard error redirection, default if null.
 */
sealed interface Stmt {
    val stdin: Filename?
    val stdout: Filename?
    val stderr: Filename?
}

/**
 * Statement can be a nonempty list of assignments.
 * @param export Assignments can be exported and be available inside external process.
 * @param assignments List of assignments.
 */
data class Assignments(
    val export: Boolean,
    val assignments: List<Assignment>,
    override val stdin: Filename?,
    override val stdout: Filename?,
    override val stderr: Filename?
) : Stmt {
    init {
        require(assignments.isNotEmpty())
    }
}

/**
 * Statement can be a nonempty sequence of commands connected with pipes.
 * @param localAssignments List of local variables that are available in the command processes.
 * @param pipeline List of atoms -- commands itself with their arguments.
 */
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

/**
 * Represents variable assignment.
 */
data class Assignment(
    val name: VarName,
    val value: VarValue
)

/**
 * Represents single command with its arguments.
 */
data class PipelineAtom(
    val name: CommandName,
    val args: List<CommandArg>
)
