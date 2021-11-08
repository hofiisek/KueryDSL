package kuery.utils

/**
 * @author Dominik Hoftych
 */

fun placeholders(n: Int) = List(n) { "?" }.joinToString(",")

fun String.replacePlaceholders(values: List<Any?>): String = if (count { it.isPlaceholder() } != values.size) {
    throw IllegalArgumentException("Number of placeholders doesn't match the number of parameters")
} else {
    var idx = 0
    map {
        if (it.isPlaceholder()) values[idx++]?.toString() ?: "NULL" else it
    }.joinToString("")
}

private fun Char.isPlaceholder() = '?' == this
