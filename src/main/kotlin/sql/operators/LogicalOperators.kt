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

    override fun toSqlOneliner(): String {
        val separator = when (this) {
            is AndOperator -> " AND "
            is OrOperator -> " OR "
            is XorOperator -> " XOR "
            is SimpleOperator, is NotOperator -> throw IllegalArgumentException(
                "Own implementation must be provided for SimpleOperator and NotOperator"
            )
        }

        return conditions.joinToString(
            separator = separator,
            prefix = "(".takeIf { parent != null }.orEmpty(),
            postfix = ")".takeIf { parent != null }.orEmpty()
        ) { it.toSqlOneliner() }
    }

}

data class SimpleOperator(private val conditionWithParam: ConditionWithParam) : LogicalOperator() {
    override val parent: LogicalOperator? = null
    override fun toSqlOneliner() = conditionWithParam.condition
}

data class AndOperator(override val parent: LogicalOperator? = null) : LogicalOperator()
data class OrOperator(override val parent: LogicalOperator? = null) : LogicalOperator()
data class XorOperator(override val parent: LogicalOperator? = null) : LogicalOperator()
data class NotOperator(override val parent: LogicalOperator? = null) : LogicalOperator() {
    override fun toSqlOneliner() = if (conditions.size == 1)
        conditions.joinToString(separator = " AND ", prefix = "NOT ") { it.toSqlOneliner() }
    else
        conditions.joinToString(separator = " AND ", prefix = "NOT (", postfix = ")") { it.toSqlOneliner() }
}

enum class OperatorType {
    AND, OR, XOR, NOT
}
