package kuery.clauses

import kuery.ParameterizedSqlizable

/**
 * @author Dominik Hoftych
 */
class LimitClause(private val limit: Long) : ParameterizedSqlizable() {
    override fun toSqlOneliner() = "LIMIT ?".also { params.add(limit) }
}
