package ru.itmo.sd.nebash.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile

fun <T, R> Flow<T?>.mapWhileNotNull(transform: suspend (T) -> R): Flow<R> =
    takeWhile { it != null }.map { require(it != null); transform(it) }

suspend fun <T> Flow<T?>.collectWhileNotNull(block: suspend (T) -> Unit) =
    takeWhile { it != null }.collect { require(it != null); block(it) }
