package sql.statements

import sql.functions.NameWithAlias

/**
 * @author Dominik Hoftych
 */
class SelectStatement : Statement() {

    private val columns: MutableList<NameWithAlias> = mutableListOf()

    fun all() = +"*"

    operator fun String.unaryPlus() = columns.add(NameWithAlias(this))
    operator fun NameWithAlias.unaryPlus() = columns.add(this)

    override fun toSql() = "SELECT ${columns.joinToString(separator = ", ")}"
}
