package sql.functions

/**
 * @author Dominik Hoftych
 */
fun String.gt(than: String) = ConditionWithParam("$this > ?", than)
fun String.gte(than: String) = ConditionWithParam("$this >= ?", than)

fun String.lt(than: String) = ConditionWithParam("$this < ?", than)
fun String.lte(than: String) = ConditionWithParam("$this <= ?", than)

fun String.eq(to: String) = ConditionWithParam("$this = ?", to)
fun String.neq(to: String) = ConditionWithParam("$this != ?", to)

fun String.isNull() = ConditionWithParam("$this IS ?", "NULL")

fun String.between(first: String, second: String) = ConditionWithParam("$this BETWEEN ? AND ?", listOf(first, second))

fun String.`in`(vararg values: String) = ConditionWithParam(
    "$this IN(${values.joinToString(",") { "?" }})",
    values.asList()
)

class ConditionWithParam(val condition: String, val param: Any?)

fun ConditionWithParam.not() = ConditionWithParam("NOT $condition", param)
