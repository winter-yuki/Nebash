package ru.itmo.sd.nebash.utils

inline fun CharSequence.indexOfLastOrNull(block: (Char) -> Boolean): Int? {
    val idx = indexOfLast(block)
    return if (idx == -1) null else idx
}
