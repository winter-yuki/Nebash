package ru.itmo.sd.nebash

import ru.itmo.sd.nebash.backend.execute
import ru.itmo.sd.nebash.frontend.raw.RawStmt
import ru.itmo.sd.nebash.frontend.stmt.parse
import ru.itmo.sd.nebash.frontend.subst.substitute

class Nebash(private val env: MutableEnv = MutableEnvImpl()) {
    fun execute(stmt: RawStmt) {
        val substituted = stmt.substitute(env)
        val parsed = parse(substituted)
        parsed.execute(env)
    }
}
