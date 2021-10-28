package sql

import sql.clauses.*
import sql.operators.LogicalOperator
import sql.operators.OperatorType
import sql.statements.SelectStatement
import sql.statements.Statement

/**
 * @author Dominik Hoftych
 */
class SqlStatement {

    private lateinit var statement: Statement
    private var from: FromClause? = null
    private var where: WhereClause? = null
    private var orderBy: OrderByClause? = null
    private var limit: LimitClause? = null
    private var offset: OffsetClause? = null

    val queryOneliner: String
        get() = listOfNotNull(statement, from, where, orderBy, limit, offset).joinToString(" ") { it.toSql() }

    val queryFormatted: String
        get() = listOfNotNull(statement, from, where, orderBy, limit, offset).joinToString("\n") { it.toSql() }

    val params: List<Any?>
        get() = listOfNotNull(statement, from, where, orderBy, limit, offset)
            .filterIsInstance<ParameterizedSqlizable>()
            .flatMap { it.params }

    fun select(block: SelectStatement.() -> Unit) = SelectStatement()
        .apply(block)
        .also { statement = it }

    fun from(block: FromClause.() -> Unit) = FromClause().apply(block).also { from = it }

    fun where(operator: OperatorType = OperatorType.AND, block: LogicalOperator.() -> Unit) = WhereClause()
        .apply {
            when (operator) {
                OperatorType.AND -> and(block)
                OperatorType.OR -> or(block)
                OperatorType.XOR -> xor(block)
                OperatorType.NOT -> throw IllegalArgumentException("NOT operator not supported as root operator")
            }
        }.also { where = it }

    fun orderBy(block: OrderByClause.() -> Unit) = OrderByClause().apply(block).also { orderBy = it }
    // TODO group by
    // TODO having

    fun limit(limit: Long) = let { this.limit = LimitClause(limit) }
    fun offset(offset: Long) = let { this.offset = OffsetClause(offset) }

    override fun toString() = queryFormatted
}

fun sqlStatement(block: SqlStatement.() -> Unit): SqlStatement = SqlStatement().apply(block)
