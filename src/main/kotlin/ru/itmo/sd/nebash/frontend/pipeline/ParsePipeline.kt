package ru.itmo.sd.nebash.frontend.pipeline

import ru.itmo.sd.nebash.PipelineAtom
import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.frontend.EmptyPipelineAtomException
import ru.itmo.sd.nebash.runtime.ca
import ru.itmo.sd.nebash.runtime.toCn
import ru.itmo.sd.nebash.utils.split

fun String.parsePipeline(state: State): List<PipelineAtom> {
    if (isBlank()) return listOf()
    val tokens = tokenize().map { it.substitute(state) }
    val atoms = tokens.split({ it is Pipe }) { token ->
        when (token) {
            is Part -> token.s.trim().split("""\s+""".toRegex())
            is SingleQuoted -> listOf(token.s)
            is DoubleQuoted -> listOf(token.s)
            is Pipe -> error("Pipes should have been split of")
        }
    }
    return atoms.map { atom ->
        PipelineAtom(
            name = atom.firstOrNull()
                ?.ifEmpty { null }?.toCn()
                ?: throw EmptyPipelineAtomException(),
            args = atom.drop(1).map { it.ca }
        )
    }
}
