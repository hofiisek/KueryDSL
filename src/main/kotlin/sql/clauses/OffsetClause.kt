package sql.clauses

import sql.ParameterizedSqlizable

/**
 * @author Dominik Hoftych
 */
class OffsetClause(private val offset: Long) : ParameterizedSqlizable() {
    override fun toSql() = "OFFSET ?".also { params.add(offset) }
}
