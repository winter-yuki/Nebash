package ru.itmo.sd.nebash

/**
 * Represents command execution environment.
 */
class Env(val map: Map<VarName, VarValue>) : Map<VarName, VarValue> by map

operator fun Env.plus(other: Map<VarName, VarValue>) = Env(map + other)

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

private class MutableStateImpl(private val map: MutableMap<VarName, VarValue> = mutableMapOf()) :
    MutableState, MutableMap<VarName, VarValue> by map {

    private val systemEnv = System.getenv()
        .map { (name, value) -> name.toVn() to value.vv }
        .associate { it }.toMutableMap()
    private val exportedSet: MutableSet<VarName> = mutableSetOf()

    override fun export(name: VarName) {
        exportedSet += name
    }

    override fun get(key: VarName): VarValue? = map[key] ?: systemEnv[key]

    override val env: Env
        get() {
            val exported = map.filterKeys { it in exportedSet }
            return Env(systemEnv + exported)
        }
}

/**
 * [State] builder function.
 */
fun State(): State = MutableStateImpl()

/**
 * [MutableState] builder function.
 */
fun MutableState(): MutableState = MutableStateImpl()

/**
 * [MutableState] builder function.
 */
fun MutableState(vararg map: Pair<VarName, VarValue>): MutableState =
    MutableStateImpl(map = map.associate { it }.toMutableMap())

/**
 * [MutableState] builder function.
 */
@JvmName("StringMutableState")
fun MutableState(vararg map: Pair<String, String>): MutableState =
    MutableStateImpl(map = map.associate { (name, value) -> name.toVn() to value.vv }.toMutableMap())

@JvmInline
value class VarName(val name: String) {
    init {
        require(name.isNotBlank())
    }
}

fun String.toVn() = VarName(this)

@JvmInline
value class VarValue(val value: String) {
    override fun toString(): String = value
}

val String.vv: VarValue
    get() = VarValue(this)
