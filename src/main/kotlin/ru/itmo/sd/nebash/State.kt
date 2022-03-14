package ru.itmo.sd.nebash

/**
 * Represents command execution environment.
 */
class Env(map: Map<VarName, VarValue>) : Map<VarName, VarValue> by (System.getenv().bimap() + map)

private fun MutableMap<String, String>.bimap(): Map<VarName, VarValue> =
    map { (name, value) -> name.vn to value.vv }.associate { it }

/**
 * Represents interpreter state that contain variables.
 * Some variables are *exported*: available in child processes.
 */
interface State : Map<VarName, VarValue> {

    /**
     * Subset of variables that are exported.
     */
    val env: Env
}

/**
 * Represents mutable [State].
 */
interface MutableState : State, MutableMap<VarName, VarValue> {
    fun export(name: VarName)
}

private class MutableStateImpl(
    private val parent: State? = null,
    private val map: MutableMap<VarName, VarValue> = mutableMapOf()
) : MutableState, MutableMap<VarName, VarValue> by map {

    private val exportedSet: MutableSet<VarName> = mutableSetOf()

    override fun export(name: VarName) {
        exportedSet += name
    }

    override fun get(key: VarName): VarValue? = map[key] ?: parent?.get(key)

    override val env: Env
        get() {
            val exported = map.filterKeys { it in exportedSet }
            val joined = parent?.env.orEmpty() + exported
            return Env(joined)
        }
}

/**
 * [State] builder function.
 */
fun State(parent: State? = null): State = MutableState(parent)

/**
 * [MutableState] builder function.
 */
fun MutableState(parent: State? = null): MutableState = MutableStateImpl(parent)

/**
 * [MutableState] builder function.
 */
fun MutableState(vararg map: Pair<VarName, VarValue>): MutableState =
    MutableStateImpl(map = map.associate { it }.toMutableMap())

/**
 * [MutableState] builder function.
 */
@JvmName("RawMutableState")
fun MutableState(vararg map: Pair<String, String>): MutableState =
    MutableStateImpl(map = map.associate { (name, value) -> name.vn to value.vv }.toMutableMap())

@JvmInline
value class VarName(val name: String) {
    init {
        require(name.isNotBlank())
    }
}

val String.vn: VarName
    get() = VarName(this)

@JvmInline
value class VarValue(val value: String) {
    override fun toString(): String = value
}

val String.vv: VarValue
    get() = VarValue(this)
