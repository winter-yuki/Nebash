package ru.itmo.sd.nebash

interface Env {
    operator fun get(name: VarName): VarValue?
    val exported: Map<VarName, VarValue>
}

interface MutableEnv : Env {
    operator fun set(name: VarName, value: VarValue)
    fun export(name: VarName)
}

class MutableEnvImpl(private val parent: Env? = null) : MutableEnv {

    private val map: MutableMap<VarName, VarValue> = mutableMapOf()
    private val exportedSet: MutableSet<VarName> = mutableSetOf()

    override fun set(name: VarName, value: VarValue) {
        map[name] = value;
    }

    override fun export(name: VarName) {
        exportedSet += name;
    }

    override fun get(name: VarName): VarValue? = map[name] ?: parent?.get(name)

    override val exported: Map<VarName, VarValue>
        get() {
            val exportedMap = map.filterKeys { it in exportedSet }
            return parent?.exported.orEmpty() + exportedMap
        }
}

@JvmInline
value class VarName(val name: String) {
    init {
        require(name.isNotBlank())
    }
}

val String.vn: VarName
    get() = VarName(this)

@JvmInline
value class VarValue(val value: String)

val String.vv: VarValue
    get() = VarValue(this)
