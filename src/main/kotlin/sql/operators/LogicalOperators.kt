package sql.operators

import sql.ParameterizedSqlizable
import sql.functions.ConditionWithParam

sealed class LogicalOperator : ParameterizedSqlizable() {

    abstract val parent: LogicalOperator?
    protected val conditions: MutableList<LogicalOperator> = mutableListOf()

    fun and(block: AndOperator.() -> Unit) = AndOperator(parent = this).apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun or(block: OrOperator.() -> Unit) = OrOperator(parent = this).apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun xor(block: XorOperator.() -> Unit) = XorOperator(parent = this).apply(block).also {
        conditions.add(it)
        params.addAll(it.params)
    }

    fun not(operator: OperatorType = OperatorType.AND, block: LogicalOperator.() -> Unit) = NotOperator(parent = this)
        .apply {
            when (operator) {
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
    override val parent: LogicalOperator? = null
    override fun toSql() = conditionWithParam.condition
}

class AndOperator(override val parent: LogicalOperator? = null) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " AND ",
        prefix = "(".takeIf { parent != null }.orEmpty(),
        postfix = ")".takeIf { parent != null }.orEmpty()
    ) { it.toSql() }
}

class OrOperator(override val parent: LogicalOperator? = null) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " OR ",
        prefix = "(".takeIf { parent != null }.orEmpty(),
        postfix = ")".takeIf { parent != null }.orEmpty()
    ) { it.toSql() }
}

class XorOperator(override val parent: LogicalOperator? = null) : LogicalOperator() {
    override fun toSql() = conditions.joinToString(
        separator = " XOR ",
        prefix = "(".takeIf { parent != null }.orEmpty(),
        postfix = ")".takeIf { parent != null }.orEmpty()
    ) { it.toSql() } 
}

class NotOperator(override val parent: LogicalOperator? = null) : LogicalOperator() {
    override fun toSql() = if (conditions.size == 1)
        conditions.joinToString(separator = " AND ", prefix = "NOT ") { it.toSql() }
    else
        conditions.joinToString(separator = " AND ", prefix = "NOT (", postfix = ")") { it.toSql() }
}

enum class OperatorType {
    AND, OR, XOR, NOT
}
