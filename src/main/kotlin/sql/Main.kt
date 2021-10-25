package sql

import sql.functions.alias
import sql.functions.eq
import sql.functions.isNotNull
import sql.functions.isNull
import sql.operators.OperatorType

/**
 * TODO joins
 *
 * TODO finalize first/last brackets design
 * TODO write tests for where query
 * TODO create Aliasable with default empty alias to restrict calling nonsense
 *  operators in select? (restrict calling .gt(..) on +"name".alias("name)")
 *
 * TODO better query formatting
 *
 * TODO allow to prefix column with src table's alias?
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
        where(rootOperator = OperatorType.OR) {
//            +"p.age".gt(25)
            not(rootOperator = OperatorType.OR) {
                +"age".isNotNull()
                +"age".isNull()
            }
//            or {
//                +"p.name".eq("Joe")
//                +"p.name".eq("Josh")
//                and {
//                    +"r.first_name".eq("Sheldon")
//                    +"r.surname".eq("Cooper")
//                    +"r.age".between(30, 50).not()
//                    +"r.age".`in`("1", 2, 3)
//                }
//            }
        }
    }

    println(stmt.query)
    println()
    println("parameters: ${stmt.params}")
}
