package sql.clauses

import sql.Sqlizable
import sql.functions.NameWithAlias

/**
 * @author Dominik Hoftych
 */
class FromClause : Sqlizable {

    private val tables: MutableList<NameWithAlias> = mutableListOf()
    private val joins: MutableList<JoinClause> = mutableListOf()

    fun leftJoin(table: String, alias: String = "") = LeftJoin(NameWithAlias(table, alias)).also { joins.add(it) }
    fun rightJoin(table: String, alias: String = "") = RightJoin(NameWithAlias(table, alias)).also { joins.add(it) }
    fun innerJoin(table: String, alias: String = "") = InnerJoin(NameWithAlias(table, alias)).also { joins.add(it) }
    fun fullJoin(table: String, alias: String = "") = FullJoin(NameWithAlias(table, alias)).also { joins.add(it) }

    operator fun String.unaryPlus() = tables.add(NameWithAlias(this))
    operator fun NameWithAlias.unaryPlus() = tables.add(this)

    override fun toSqlOneliner() = buildString {
        append("FROM ${tables.joinToString(", ")}")
        if (joins.isNotEmpty()) {
            append(" ")
            append(joins.joinToString(" ") { it.toSqlOneliner() })
        }
    }

    override fun toSqlFormatted() = buildString {
        append("FROM ${tables.joinToString(", ")}")
        if (joins.isNotEmpty()) {
            append("\n")
            append(joins.joinToString("\n") { it.toSqlFormatted() })
        }
    }
}
