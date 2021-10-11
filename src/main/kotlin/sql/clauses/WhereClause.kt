package sql.clauses

import sql.ParameterizedSqlable
import sql.ScopeMarker
import sql.operators.*

/**
 * @author Dominik Hoftych
 */
@ScopeMarker
class WhereClause : ParameterizedSqlable() {

    private val conditions: MutableList<LogicalOperator> = mutableListOf()

    fun and(block: AndOperator.() -> Unit, noBrackets: Boolean = false): AndOperator = AndOperator(noBrackets)
        .apply(block)
        .also {
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

    override fun toSql(): String = "WHERE ${conditions.joinToString(separator = " AND ") { it.toSql() }}"
}
