package ru.itmo.sd.nebash.frontend.subst

import ru.itmo.sd.nebash.State
import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.raw.rs

//fun RawStmt.substitute(state: State): RawStmt = buildString {
//    var state: State = Empty
//    stmt.forEach { c ->
//        when (val local = state) {
//            is Empty -> when (c) {
//                SingleQuote.repr -> {
//                    state = SingleQuote
//                    append(c)
//                }
//                Quote.repr -> {
//                    state = Quote
//                    append(c)
//                }
//                '$' -> state = Dollar()
//                else -> append(c)
//            }
//            is SingleQuote -> when (c) {
//                SingleQuote.repr -> {
//                    state = Empty
//                    append(c)
//                }
//                else -> append(c)
//            }
//            is Quote -> when (c) {
//                Quote.repr -> {
//                    state = Empty
//                    append(c)
//                }
//                '$' -> state = QuoteDollar()
//                else -> append(c)
//            }
//            is Dollar -> if (local.name.isEmpty()) {
//
//            } else {
//
//            }
//            is QuoteDollar -> TODO()
//        }
//    }
//}.rs

//private sealed interface State

//private object Empty : State
//
//private fun onEmpty(c: Char): State {
//    TODO()
//}
//
//private object SingleQuote : State {
//    const val repr = '\''
//}
//
//private fun onSingleQuote(c: Char): State {
//    TODO()
//}
//
//private object Quote : State {
//    const val repr = '\"'
//}
//
//private fun onQuote(c: Char): State {
//    TODO()
//}
//
//private class Dollar : State {
//    val name = StringBuilder()
//}
//
//private class QuoteDollar : State {
//    val name = StringBuilder()
//}
//
//private val Char.isFirst: Boolean
//    get() = equals('_') || isLetter()
