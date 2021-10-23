package sql.functions

fun count(column: String = "*") = "COUNT($column)"

fun avg(column: String) = "AVG($column)"
fun max(column: String) = "MAX($column)"
fun min(column: String) = "MIN($column)"
