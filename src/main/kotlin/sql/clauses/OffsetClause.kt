package sql.clauses

import sql.ParameterizedSqlable

/**
 * @author Dominik Hoftych
 */
class OffsetClause(private val offset: Long) : ParameterizedSqlable() {
    override fun toSql() = "OFFSET ?".also { params.add(offset) }
}
