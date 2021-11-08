package kuery.clauses

import kuery.ParameterizedSqlizable

/**
 * @author Dominik Hoftych
 */
class OffsetClause(private val offset: Long) : ParameterizedSqlizable() {
    override fun toSqlOneliner() = "OFFSET ?".also { params.add(offset) }
}
