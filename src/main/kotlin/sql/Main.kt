package sql

import sql.functions.*

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
            all()
            +avg("age").alias("avg_age")
            +"name".alias("name")
        }
        from {
            +"persons"
//            +"persons".alias("t1")
//            leftJoin("persons").using("t1")
//            leftJoin("persons").using("t1", "t2")
            leftJoin("persons").on {
                +"t1.id".eq("t2.id")
                +"t1.id".eq("t2.id")
                and {
                    +"t1.id".eq("t2.id")
                    +"t1.id".eq("t2.id")
                }
            }
        }
        where {
            +"age".`in`("1", "2", "3", "4", "5").not()
            +"age".isNull()
        }
        orderBy {
            "age".asc()
            "name".desc()
        }
        limit(10)
        offset(5)
    }

    println(stmt.query)
    println()
    println("params: ${stmt.params}")
}
