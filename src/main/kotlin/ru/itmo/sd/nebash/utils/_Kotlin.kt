package ru.itmo.sd.nebash.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.takeWhile

inline fun CharSequence.indexOfLastOrNull(block: (Char) -> Boolean): Int? {
    val idx = indexOfLast(block)
    return if (idx == -1) null else idx
}

suspend fun <T> Flow<T?>.collectWhileNotNull(block: suspend (T) -> Unit) =
    takeWhile { it != null }.collect { require(it != null); block(it) }
