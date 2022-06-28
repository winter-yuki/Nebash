package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.runtime.CommandArg
import ru.itmo.sd.nebash.runtime.CommandName
import java.nio.file.Path

/**
 * Represents [Nebash] statement.
 * @property stdin Standard input redirection, default if null.
 * @property stdout Standard output redirection, default if null.
 * @property stderr Standard error redirection, default if null.
 */
sealed interface Stmt {
    val stdin: Path?
    val stdout: Path?
    val stderr: Path?
}

/**
 * Statement can be a nonempty list of assignments.
 * @param export Assignments can be exported and be available inside external process.
 * @param assignments List of assignments.
 */
data class AssignmentStmt(
    val export: Boolean = false,
    val assignments: List<Assignment>,
    override val stdin: Path? = null,
    override val stdout: Path? = null,
    override val stderr: Path? = null
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
data class PipelineStmt(
    val localAssignments: List<Assignment> = listOf(),
    val pipeline: List<PipelineAtom>,
    override val stdin: Path? = null,
    override val stdout: Path? = null,
    override val stderr: Path? = null
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
    val args: List<CommandArg> = listOf()
)
