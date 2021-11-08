package kuery.statements

import kuery.ParameterizedSqlizable
import kuery.clauses.*
import kuery.functions.NameWithAlias
import kuery.operators.LogicalOperator
import kuery.operators.OperatorType

/**
 * @author Dominik Hoftych
 */
class SelectStatement(val distinct: Boolean = false) : Statement() {

    private val columns: MutableList<NameWithAlias> = mutableListOf()
    private var from: FromClause? = null
    private var where: WhereClause? = null
    private var groupBy: GroupByClause? = null
    private var having: HavingClause? = null
    private var orderBy: OrderByClause? = null
    private var limit: LimitClause? = null
    private var offset: OffsetClause? = null

    val params: List<Any?>
        get() = listOfNotNull(from, where, orderBy, limit, offset)
            .filterIsInstance<ParameterizedSqlizable>()
            .flatMap { it.params }

    fun from(block: FromClause.() -> Unit) = FromClause().apply(block).also { from = it }

    fun where(operator: OperatorType = OperatorType.AND, block: LogicalOperator.() -> Unit) = WhereClause()
        .apply {
            when (operator) {
                OperatorType.AND -> and(block)
                OperatorType.OR -> or(block)
                OperatorType.XOR -> xor(block)
                OperatorType.NOT -> throw IllegalArgumentException("NOT operator not supported as root operator")
            }
        }.also { where = it }

    fun orderBy(block: OrderByClause.() -> Unit) = OrderByClause().apply(block).also { orderBy = it }
    fun groupBy(block: GroupByClause.() -> Unit) = GroupByClause().apply(block).also { groupBy = it }
    fun having(block: HavingClause.() -> Unit) = HavingClause().apply(block).also { having = it }
    fun limit(limit: Long) = LimitClause(limit).also { this.limit = it }
    fun offset(offset: Long) = OffsetClause(offset).also { this.offset = it }

    operator fun String.unaryPlus() = columns.add(NameWithAlias(this))
    operator fun NameWithAlias.unaryPlus() = columns.add(this)

    fun String.withTable(nameOrAlias: String) = "$nameOrAlias.$this"

    override fun toSqlFormatted() = "SELECT ${if (distinct) "DISTINCT " else ""}" +
        "${columns.joinToString(", ")} " +
        listOfNotNull(from, where, orderBy, limit, offset).joinToString("\n") { it.toSqlFormatted() }

    override fun toSqlOneliner() = "SELECT ${if (distinct) "DISTINCT " else ""}" +
        "${columns.joinToString(", ")} " +
        listOfNotNull(from, where, orderBy, limit, offset).joinToString(" ") { it.toSqlOneliner() }
}
