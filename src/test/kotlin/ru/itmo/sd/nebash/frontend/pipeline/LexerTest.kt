package ru.itmo.sd.nebash.frontend.pipeline

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LexerTest {

    @Test
    fun empty() {
        assertEquals(listOf<Token>(), "".tokenize())
    }

    @Test
    fun test() {
        val s = """echo  '| |'  | "wc" """
        val tokens = listOf(
            Part("echo  "), SingleQuoted("| |"),
            Part("  "), Pipe, Part(" "),
            DoubleQuoted("wc"), Part(" ")
        )
        assertEquals(tokens, s.tokenize())
    }
}
