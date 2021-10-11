package sql.clauses

import sql.Sqlable
import sql.operators.AndOperator
import sql.replacePlaceholders

/**
 * @author Dominik Hoftych
 */
class FromClause : Sqlable {

    private val tables: MutableList<String> = mutableListOf()
    private val joins: MutableList<JoinClause> = mutableListOf()

    fun leftJoin(table: String): JoinClause = LeftJoin(table).also { joins.add(it) }
    fun rightJoin(table: String): JoinClause = RightJoin(table).also { joins.add(it) }
    fun innerJoin(table: String): JoinClause = InnerJoin(table).also { joins.add(it) }
    fun fullJoin(table: String): JoinClause = FullJoin(table).also { joins.add(it) }

    operator fun String.unaryPlus() = tables.add(this)

    override fun toSql() = "FROM ${tables.joinToString(", ")} ${joins.joinToString(" ") { it.toSql() }}"
}

sealed class JoinClause : Sqlable {

    abstract val table: String
    lateinit var condition: JoinCondition

    fun on(block: AndOperator.() -> Unit): AndOperator = AndOperator()
        .apply(block)
        .also {
            val sqlWithParamsInlined = it.toSql().replacePlaceholders(it.params)
            condition = On(sqlWithParamsInlined)
        }

    fun using(column: String, vararg moreColumns: String) {
        condition = Using(if (moreColumns.isEmpty()) listOf(column) else listOf(column) + moreColumns.toList())
    }
}

class LeftJoin(override val table: String) : JoinClause() {
    override fun toSql() = "LEFT JOIN $table ${condition.toSql()}"
}

class RightJoin(override val table: String) : JoinClause() {
    override fun toSql() = "RIGHT JOIN ${condition.toSql()}"
}

class InnerJoin(override val table: String) : JoinClause() {
    override fun toSql() = "INNER JOIN ${condition.toSql()}"
}

class FullJoin(override val table: String) : JoinClause() {
    override fun toSql() = "FULL JOIN ${condition.toSql()}"
}

sealed class JoinCondition : Sqlable

data class On(val condition: String) : JoinCondition() {
    override fun toSql() = "ON $condition"
}

data class Using(val columns: List<String>) : JoinCondition() {
    override fun toSql() = buildString {
        append("USING ")
        append(columns.joinToString(separator = ", ", prefix = "(", postfix = ")"))
    }
}
