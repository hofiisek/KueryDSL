package sql.operators

import sql.ParameterizedSqlizable
import sql.functions.ConditionWithParam

sealed class LogicalOperator : ParameterizedSqlizable() {

    protected val conditions: MutableList<LogicalOperator> = mutableListOf()

    fun and(block: AndOperator.() -> Unit): AndOperator = AndOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun or(block: OrOperator.() -> Unit): OrOperator = OrOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun not(block: NotOperator.() -> Unit): NotOperator = NotOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun xor(block: XorOperator.() -> Unit): XorOperator = XorOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    operator fun ConditionWithParam.unaryPlus() {
        conditions.add(SimpleOperator(this))
        when (param) {
            null -> Unit
            is Collection<*> -> params.addAll(param)
            is Array<*> -> params.addAll(param)
            else -> params.add(param)
        }
    }
}

class SimpleOperator(private val conditionWithParam: ConditionWithParam) : LogicalOperator() {
    override fun toSql() = conditionWithParam.condition
}

class AndOperator(private val noBrackets: Boolean = false) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " AND ",
        prefix = "(".takeUnless { noBrackets }.orEmpty(), // TODO do it like this?
        postfix = ")".takeUnless { noBrackets }.orEmpty()
    ) { it.toSql() }
}

class OrOperator : LogicalOperator() {
    override fun toSql() = conditions.joinToString(separator = " OR ", prefix = "(", postfix = ")") { it.toSql() }
}

class NotOperator : LogicalOperator() {
    override fun toSql() = if (conditions.size == 1)
        conditions.joinToString(separator = " AND ", prefix = "NOT ") { it.toSql() }
    else
        conditions.joinToString(separator = " AND ", prefix = "NOT (", postfix = ")") { it.toSql() }
}

class XorOperator : LogicalOperator() {
    override fun toSql() = conditions.joinToString(separator = " XOR ", prefix = "(", postfix = ")") { it.toSql() }
}
