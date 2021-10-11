package sql.functions

/**
 * @author Dominik Hoftych
 */
fun avg(column: String) = "AVG($column)"
fun count(column: String) = "COUNT($column)"
fun countAll() = "COUNT(*)"
fun max(column: String) = "MAX($column)"
fun min(column: String) = "MIN($column)"
