package kuery.clauses

import kuery.ParameterizedSqlizable

/**
 * @author Dominik Hoftych
 */
class OrderByClause : ParameterizedSqlizable() {

    private val orderColumns: LinkedHashMap<String, String> = LinkedHashMap()

    fun String.asc() = orderColumns.put(this, "ASC")
    fun String.desc() = orderColumns.put(this, "DESC")

    override fun toSqlOneliner() = "ORDER BY ${orderColumns.map { (col, dir) -> "$col $dir" }.joinToString(" ")}"
}
