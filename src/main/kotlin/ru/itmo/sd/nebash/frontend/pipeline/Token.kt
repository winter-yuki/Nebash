package ru.itmo.sd.nebash.frontend.pipeline

import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.frontend.substitute

sealed interface Token {
    fun substitute(state: State): Token
}

class Part(val s: String) : Token {
    override fun substitute(state: State): Token = Part(s.substitute(state))
}

class SingleQuoted(val s: String) : Token {
    override fun substitute(state: State): Token = this
}

class DoubleQuoted(val s: String) : Token {
    override fun substitute(state: State): Token = DoubleQuoted(s.substitute(state))
}

object Pipe : Token {
    override fun substitute(state: State): Token = Pipe
}
