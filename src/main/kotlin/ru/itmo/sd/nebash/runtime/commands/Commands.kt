package ru.itmo.sd.nebash.runtime.commands

import ru.itmo.sd.nebash.runtime.Command
import ru.itmo.sd.nebash.runtime.CommandName
import ru.itmo.sd.nebash.runtime.toCn

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
