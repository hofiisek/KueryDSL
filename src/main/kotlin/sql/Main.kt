package sql

import sql.functions.*
import sql.operators.OperatorType

/**
 *
 * TODO support table's alias as column prefix, e.g. 'p.name', where 'p' is alias for 'persons' table,
 *  + check for alias existence
 *
 * TODO implement DISTINCT
 *
 * TODO implement INSERT/DELETE/UPDATE statements
 *  --> separate clauses (orderby, limit, offset, ..) from SqlStatement to appropriate statement implementations
 *    - INSERT: INTO, VALUES clauses
 *    - DELETE: FROM, WHERE, ORDER BY, LIMIT clauses
 *    - UPDATE: WHERE, ORDER BY, LIMIT clauses
 *    - SELECT: FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT, OFFSET
 *
 * @author Dominik Hoftych
 */
fun main() {

    val stmt = sqlStatement {
        select {
            +"p.name"
            +"p.age"
            +"r.first_name".alias("r_first_name")
            +"r.surname".alias("r_surname")
        }
        from {
            +"persons".alias("p")
            innerJoin(table = "relatives", alias = "r").on {
                +"r.surname".eq("p.surname")
            }
            leftJoin(table = "surnames", alias = "s").using("id")
        }
        where {
            and {
                +"p.age".gt(25)
                +"age".isNotNull()
                +"age".isNull()
            }
            not(operator = OperatorType.OR) {
                +"age".isNotNull()
                +"age".isNull()
            }
        }
    }

    println(stmt.queryOneliner)
    println()
    println(stmt.queryFormatted)
    println()
    println("parameters: ${stmt.params}")
}
