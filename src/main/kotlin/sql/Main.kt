package sql

import sql.functions.*
import sql.operators.OperatorType

/**
 * TODO nested selects
 * TODO DISTINCT, incl. in functions such as COUNT
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

    val stmt = select(distinct = true) {
        +"age".withTable("p").alias("page")

        +"r.first_name".alias("r_first_name")
        +"r.surname".alias("r_surname")

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

    println(stmt.toSqlOneliner())
    println()
    println(stmt.toSqlFormatted())
    println()
    println("parameters: ${stmt.params}")
}
