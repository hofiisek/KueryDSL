package kuery.clauses

import kuery.Sqlizable
import kuery.functions.NameWithAlias
import kuery.operators.AndOperator
import kuery.utils.replacePlaceholders

sealed class JoinClause : Sqlizable {

    abstract val table: NameWithAlias

    // TODO condition can be ommited for join/inner join
    private lateinit var joinCondition: JoinCondition

    fun on(block: AndOperator.() -> Unit) = AndOperator()
        .apply(block)
        .also {
            val sqlWithParamsInlined = it.toSqlFormatted().replacePlaceholders(it.params)
            joinCondition = On(sqlWithParamsInlined)
        }

    fun using(column: String, vararg moreColumns: String) {
        joinCondition = Using(if (moreColumns.isEmpty()) listOf(column) else listOf(column) + moreColumns.toList())
    }

    override fun toSqlOneliner() = when (this) {
        is FullJoin -> "FULL JOIN"
        is InnerJoin -> "INNER JOIN"
        is LeftJoin -> "LEFT JOIN"
        is RightJoin -> "RIGHT JOIN"
    } + " $table ${joinCondition.toSqlOneliner()}"

    override fun toSqlFormatted() = when (this) {
        is FullJoin -> "FULL JOIN"
        is InnerJoin -> "INNER JOIN"
        is LeftJoin -> "LEFT JOIN"
        is RightJoin -> "RIGHT JOIN"
    } + " $table\n\t${joinCondition.toSqlFormatted()}"
}

data class FullJoin(override val table: NameWithAlias) : JoinClause()
data class InnerJoin(override val table: NameWithAlias) : JoinClause()
data class LeftJoin(override val table: NameWithAlias) : JoinClause()
data class RightJoin(override val table: NameWithAlias) : JoinClause()

sealed class JoinCondition : Sqlizable

data class On(val condition: String) : JoinCondition() {
    override fun toSqlOneliner() = "ON $condition"
}

data class Using(val columns: List<String>) : JoinCondition() {
    override fun toSqlOneliner() = "USING ${columns.joinToString(separator = ", ", prefix = "(", postfix = ")")}"
}
