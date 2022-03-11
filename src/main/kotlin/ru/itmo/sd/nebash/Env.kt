package ru.itmo.sd.nebash

/**
 * Represents environments that contain variables.
 * Some variables are *exported*: available in child processes.
 */
interface Env : Map<VarName, VarValue> {

    /**
     * Subset of variables that are exported.
     */
    val exported: Env
}

/**
 * Represents mutable [Env].
 */
interface MutableEnv : Env, MutableMap<VarName, VarValue> {
    fun export(name: VarName)
}

private class MutableEnvImpl(
    private val parent: Env? = null,
    private val map: MutableMap<VarName, VarValue> = mutableMapOf()
) : MutableEnv, MutableMap<VarName, VarValue> by map {

    private val exportedSet: MutableSet<VarName> = mutableSetOf()

    override fun export(name: VarName) {
        exportedSet += name;
    }

    override fun get(key: VarName): VarValue? = map[key] ?: parent?.get(key)

    override val exported: Env
        get() {
            val exported = map.filterKeys { it in exportedSet }
            val joined = parent?.exported.orEmpty() + exported
            return MutableEnvImpl(map = joined.toMutableMap())
        }
}

/**
 * [Env] builder function.
 */
fun Env(parent: Env? = null): Env = MutableEnv(parent)

/**
 * [MutableEnv] builder function.
 */
fun MutableEnv(parent: Env? = null): MutableEnv = MutableEnvImpl(parent)

@JvmInline
value class VarName(val name: String)

val String.vn: VarName
    get() = VarName(this)

@JvmInline
value class VarValue(val value: String)

val String.vv: VarValue
    get() = VarValue(this)
