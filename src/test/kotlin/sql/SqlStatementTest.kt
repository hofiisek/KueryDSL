package sql

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import sql.functions.alias
import sql.functions.avg
import sql.functions.countAll

/**
 * @author Dominik Hoftych
 */
internal class SqlStatementTest : StringSpec({

    "generated select clause is query" {
        val expected = "SELECT age, name AS name, COUNT(*) AS cnt, AVG(age) as avg_age FROM persons"
        val stmt = sqlStatement {
            select {
                +"age"
                +"name".alias("name")
                +countAll().alias("cnt")
                +avg("age").alias("avg_age")
                from {
                    +"persons"
                }
            }
        }

        stmt.query shouldBe expected
    }

    "generated where clause is valid" {
        val stmt = sqlStatement {
            select { }
        }
    }
})
