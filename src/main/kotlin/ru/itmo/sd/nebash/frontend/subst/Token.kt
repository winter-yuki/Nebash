package ru.itmo.sd.nebash.frontend.subst

sealed interface Token

class Part(val s: String) : Token

class Quoted(val s: String) : Token
