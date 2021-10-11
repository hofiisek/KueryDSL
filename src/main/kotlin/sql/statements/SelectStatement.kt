package sql.statements

/**
 * @author Dominik Hoftych
 */
class SelectStatement : Statement() {

    private val clauses: MutableList<String> = mutableListOf()

    fun all(): Unit = let { clauses.add("*") }

    operator fun String.unaryPlus() = clauses.add(this)

    override fun toSql() = "SELECT ${clauses.joinToString(separator = ", ")}"
}
