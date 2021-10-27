package sql.operators

import sql.ParameterizedSqlizable
import sql.functions.ConditionWithParam

sealed class LogicalOperator : ParameterizedSqlizable() {

    protected val conditions: MutableList<LogicalOperator> = mutableListOf()

    fun and(block: AndOperator.() -> Unit) = AndOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun or(block: OrOperator.() -> Unit) = OrOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun xor(block: XorOperator.() -> Unit) = XorOperator().apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun not(rootOperator: OperatorType = OperatorType.AND, block: LogicalOperator.() -> Unit) = NotOperator()
        .apply {
            when (rootOperator) {
                OperatorType.AND -> and(block)
                OperatorType.OR -> or(block)
                OperatorType.XOR -> xor(block)
                OperatorType.NOT -> throw IllegalArgumentException("NOT operator not supported as root operator")
            }
        }.also {
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

class AndOperator(private val omitBrackets: Boolean = false) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " AND ",
        prefix = "(".takeUnless { omitBrackets }.orEmpty(),
        postfix = ")".takeUnless { omitBrackets }.orEmpty()
    ) { it.toSql() }
}

class OrOperator(private val omitBrackets: Boolean = false) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " OR ",
        prefix = "(".takeUnless { omitBrackets }.orEmpty(),
        postfix = ")".takeUnless { omitBrackets }.orEmpty()
    ) { it.toSql() } 
}

class XorOperator(private val omitBrackets: Boolean = false) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " XOR ",
        prefix = "(".takeUnless { omitBrackets }.orEmpty(),
        postfix = ")".takeUnless { omitBrackets }.orEmpty()
    ) { it.toSql() } 
}

class NotOperator : LogicalOperator() {
    override fun toSql() = if (conditions.size == 1)
        conditions.joinToString(separator = " AND ", prefix = "NOT ") { it.toSql() }
    else
        conditions.joinToString(separator = " AND ", prefix = "NOT (", postfix = ")") { it.toSql() }
}

enum class OperatorType {
    AND, OR, XOR, NOT
}
