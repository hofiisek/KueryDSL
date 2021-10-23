package sql.clauses

import sql.ParameterizedSqlizable

/**
 * TODO sql injection?
 *
 * @author Dominik Hoftych
 */
class OrderByClause : ParameterizedSqlizable() {

    private val orderColumns: LinkedHashMap<String, String> = LinkedHashMap()

    fun String.asc() = orderColumns.put(this, "ASC")
    fun String.desc() = orderColumns.put(this, "DESC")

    override fun toSql() = "ORDER BY ${orderColumns.map { (col, dir) -> "$col $dir" }.joinToString(" ")}"
}
