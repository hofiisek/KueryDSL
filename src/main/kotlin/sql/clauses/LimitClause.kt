package sql.clauses

import sql.ParameterizedSqlable

/**
 * @author Dominik Hoftych
 */
class LimitClause(private val limit: Long) : ParameterizedSqlable() {
    override fun toSql() = "LIMIT ?".also { params.add(limit.toString()) }
}
