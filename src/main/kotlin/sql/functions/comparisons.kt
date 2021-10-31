package sql.functions

import sql.utils.placeholders

/**
 * @author Dominik Hoftych
 */
fun String.gt(than: Any) = SingleParamCondition("$this > ?", than)
fun String.gte(than: Any) = SingleParamCondition("$this >= ?", than)

fun String.lt(than: Any) = SingleParamCondition("$this < ?", than)
fun String.lte(than: Any) = SingleParamCondition("$this <= ?", than)

fun String.eq(to: Any) = SingleParamCondition("$this = ?", to)
fun String.neq(to: Any) = SingleParamCondition("$this != ?", to)

fun String.isNull() = NoParamCondition("$this IS NULL")
fun String.isNotNull() = NoParamCondition("$this IS NOT NULL")

fun String.between(first: Any, second: Any) = MultiParamCondition("$this BETWEEN ? AND ?", listOf(first, second))

fun String.`in`(vararg values: Any) = if (values.isNotEmpty())
    MultiParamCondition("$this IN(${placeholders(values.size)})", values.asList())
else
    throw IllegalArgumentException("Provide at least 1 value")

