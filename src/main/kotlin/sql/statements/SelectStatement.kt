package sql.statements

import sql.functions.NameWithAlias

/**
 * @author Dominik Hoftych
 */
class SelectStatement : Statement() {

    private val clauses: MutableList<NameWithAlias> = mutableListOf()

    operator fun String.unaryPlus() = clauses.add(NameWithAlias(this))
    operator fun NameWithAlias.unaryPlus() = clauses.add(this)

    override fun toSql() = "SELECT ${clauses.joinToString(separator = ", ")}"
}
