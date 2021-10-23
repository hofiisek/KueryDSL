package sql.clauses

import sql.Sqlizable
import sql.functions.NameWithAlias
import sql.operators.AndOperator
import sql.replacePlaceholders

sealed class JoinClause : Sqlizable {

    abstract val table: NameWithAlias

    // TODO condition can be ommited for join/inner join
    lateinit var condition: JoinCondition

    fun on(block: AndOperator.() -> Unit) = AndOperator()
        .apply(block)
        .also {
            val sqlWithParamsInlined = it.toSql().replacePlaceholders(it.params)
            condition = On(sqlWithParamsInlined)
        }

    fun using(column: String, vararg moreColumns: String) {
        condition = Using(if (moreColumns.isEmpty()) listOf(column) else listOf(column) + moreColumns.toList())
    }

    override fun toSql() = when (this) {
        is FullJoin -> "FULL JOIN"
        is InnerJoin -> "INNER JOIN"
        is LeftJoin -> "LEFT JOIN"
        is RightJoin -> "RIGHT JOIN"
    } + " $table\n\t${condition.toSql()}"
}

data class FullJoin(override val table: NameWithAlias) : JoinClause()
data class InnerJoin(override val table: NameWithAlias) : JoinClause()
data class LeftJoin(override val table: NameWithAlias) : JoinClause()
data class RightJoin(override val table: NameWithAlias) : JoinClause()

sealed class JoinCondition : Sqlizable

data class On(val condition: String) : JoinCondition() {
    override fun toSql() = "ON $condition"
}

data class Using(val columns: List<String>) : JoinCondition() {
    override fun toSql() = buildString {
        append("USING ")
        append(columns.joinToString(separator = ", ", prefix = "(", postfix = ")"))
    }
}
