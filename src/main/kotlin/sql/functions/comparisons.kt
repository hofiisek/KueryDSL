package sql.functions

typealias NamedParam = Pair<String, Any?>

/**
 * @author Dominik Hoftych
 */
fun String.gt(than: Any) = ConditionWithParam("$this > ?", than)
fun String.gte(than: Any) = ConditionWithParam("$this >= ?", than)

fun String.lt(than: Any) = ConditionWithParam("$this < ?", than)
fun String.lte(than: Any) = ConditionWithParam("$this <= ?", than)

fun String.eq(to: Any) = ConditionWithParam("$this = ?", to)
fun String.neq(to: Any) = ConditionWithParam("$this != ?", to)

fun String.isNull() = ConditionWithParam("$this IS NULL")
fun String.isNotNull() = ConditionWithParam("$this IS NOT NULL")

fun String.between(first: Any, second: Any) = ConditionWithParam("$this BETWEEN ? AND ?", listOf(first, second))

fun String.`in`(vararg values: Any) = if (values.isEmpty())
    throw IllegalArgumentException("Provide at least 1 value")
else ConditionWithParam(
    condition = "$this IN(${List(values.size) { "?" }.joinToString(",")})",
    param = values.asList()
)

// TODO or just class with vararg params?
data class ConditionWithParam(val condition: String, val param: Any? = null)

fun ConditionWithParam.not() = ConditionWithParam("NOT $condition", param)
