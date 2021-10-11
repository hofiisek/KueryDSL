package sql

import sql.clauses.*
import sql.operators.AndOperator
import sql.operators.OrOperator
import sql.statements.SelectStatement
import sql.statements.Statement

/**
 * @author Dominik Hoftych
 */
@ScopeMarker
class SqlStatement {

    private lateinit var statement: Statement
    private var from: FromClause? = null
    private var where: WhereClause? = null
    private var orderBy: OrderByClause? = null
    private var limit: LimitClause? = null
    private var offset: OffsetClause? = null

    val query: String
        get() = listOfNotNull(statement, from, where, orderBy, limit, offset)
            .joinToString("\n") { it.toSql() }

    val params: List<Any?>
        get() = listOfNotNull(statement, from, where, orderBy, limit, offset)
            .filterIsInstance<ParameterizedSqlable>()
            .flatMap { it.params }

    fun select(block: SelectStatement.() -> Unit): SelectStatement = SelectStatement()
        .apply(block)
        .also { statement = it }
//    fun insert(block: InsertStatement.() -> Unit): InsertStatement = InsertStatement().apply(block).also { statement = it }
//    fun delete(block: DeleteStatement.() -> Unit): DeleteStatement = DeleteStatement().apply(block).also { statement = it }
//    fun update(block: UpdateStatement.() -> Unit): UpdateStatement F= UpdateStatement().apply(block).also { statement = it }

    fun from(block: FromClause.() -> Unit): FromClause = FromClause().apply(block).also { from = it }

    // TODO return where clause or directly AndOperator??
    fun where(block: AndOperator.() -> Unit): WhereClause = WhereClause()
        .apply { and(block, true) }
        .also { where = it }
    fun whereOr(block: OrOperator.() -> Unit): WhereClause = WhereClause().apply { or(block) }.also { where = it }

    fun orderBy(block: OrderByClause.() -> Unit): OrderByClause = OrderByClause().apply(block).also { orderBy = it }
    // TODO group by
    // TODO having

    fun limit(limit: Long) = let { this.limit = LimitClause(limit) }
    fun offset(offset: Long) = let { this.offset = OffsetClause(offset) }

    override fun toString() = query
}

fun sqlStatement(block: SqlStatement.() -> Unit): SqlStatement = SqlStatement().apply(block)
