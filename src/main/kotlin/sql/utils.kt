package sql

/**
 * @author Dominik Hoftych
 */
// fun String.replacePlaceholders(values: List<Any?>): String {
//    require(count { it.isPlaceholder() } == values.size) {
//        "Number of placeholders doesn't match the number of parameters"
//    }
//
//    var idx = 0
//    return map {
//        if (it.isPlaceholder()) values[idx++]?.toString() ?: "NULL" else it
//    }.joinToString("")
// }

fun String.replacePlaceholders(values: List<Any?>): String = if (count { it.isPlaceholder() } != values.size) {
    throw IllegalArgumentException("Number of placeholders doesn't match the number of parameters")
} else {
    var idx = 0
    map {
        if (it.isPlaceholder()) values[idx++]?.toString() ?: "NULL" else it
    }.joinToString("")
}

private fun Char.isPlaceholder() = '?' == this
