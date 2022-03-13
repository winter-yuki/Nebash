package ru.itmo.sd.nebash.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.takeWhile

suspend fun <T> Flow<T?>.collectWhileNotNull(block: suspend (T) -> Unit) =
    takeWhile { it != null }.collect { require(it != null); block(it) }

inline fun <T, R> List<T>.split(p: (T) -> Boolean, transform: (T) -> List<R>): List<List<R>> {
    val res = mutableListOf<List<R>>()
    var part = mutableListOf<T>()
    forEach {
        if (p(it)) {
            res += part.flatMap(transform)
            part = mutableListOf()
        } else {
            part += it
        }
    }
    res += part.flatMap(transform)
    return res
}

val unreachable: Nothing
    get() = error("Unreachable code reached")
