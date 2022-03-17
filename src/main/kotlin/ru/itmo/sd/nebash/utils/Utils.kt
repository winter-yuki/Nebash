package ru.itmo.sd.nebash.utils

import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter

@JvmInline
value class Filename(private val name: String) {
    fun bufferedReader(): BufferedReader = Path.of(name).bufferedReader()
    fun bufferedWriter(): BufferedWriter = Path.of(name).bufferedWriter()
}
