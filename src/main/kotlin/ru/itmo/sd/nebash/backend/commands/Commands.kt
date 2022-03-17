package ru.itmo.sd.nebash.backend.commands

import ru.itmo.sd.nebash.backend.Command
import ru.itmo.sd.nebash.backend.CommandName
import ru.itmo.sd.nebash.backend.toCn

/**
 * Provides command by its name.
 */
fun commandByName(name: CommandName): Command =
    commands[name] ?: External(name)

private val commands = mapOf(
    "cat".toCn() to Cat,
    "echo".toCn() to Echo,
    "pwd".toCn() to Pwd,
    "wc".toCn() to Wc
)
