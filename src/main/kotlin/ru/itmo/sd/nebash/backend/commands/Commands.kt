package ru.itmo.sd.nebash.backend.commands

import ru.itmo.sd.nebash.backend.Command
import ru.itmo.sd.nebash.backend.CommandName
import ru.itmo.sd.nebash.backend.cn

private val commands = mapOf(
    "cat".cn to Cat,
    "echo".cn to Echo,
    "pwd".cn to Pwd,
    "wc".cn to Wc
)

fun commandByName(name: CommandName): Command =
    commands[name] ?: External(name)
