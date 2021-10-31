package sql

import sql.statements.SelectStatement

fun select(distinct: Boolean = false, block: SelectStatement.() -> Unit) = SelectStatement(distinct).apply(block)
