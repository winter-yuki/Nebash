package ru.itmo.sd.nebash.utils

class ResourceStringNotFoundException(name: String) : RuntimeException("Resource with name \"$name\" not found")

/**
 * Get string of the system lang from the resources.
 */
fun Any.getString(vararg names: String): String {
    val lang = System.getenv()["LANG"]?.take(2) ?: "en"
    val full = "/$lang/" + names.joinToString("/")
    return javaClass.getResource(full)?.readText() ?: throw ResourceStringNotFoundException(full)
}
