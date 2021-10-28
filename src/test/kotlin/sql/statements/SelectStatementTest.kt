package sql.statements

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import sql.functions.*
import sql.sqlStatement

/**
 * @author Dominik Hoftych
 */
internal class SelectStatementTest : StringSpec({

    "simple select query test" {
        val expected = "SELECT age, name AS name FROM persons"
        val stmt = sqlStatement {
            select {
                +"age"
                +"name".alias("name")
                from {
                    +"persons"
                }
            }
        }

        stmt.queryOneliner shouldBe expected
    }

    "select query with aliases and aggregate functions test" {
        val expected = "SELECT age, name AS name, COUNT(*) AS cnt, AVG(age) AS avg_age FROM persons"
        val stmt = sqlStatement {
            select {
                +"age"
                +"name".alias("name")
                +count().alias("cnt")
                +avg("age").alias("avg_age")
                from {
                    +"persons"
                }
            }
        }

        stmt.queryOneliner shouldBe expected
    }

    "select from multiple tables test (implicit join)" {
        val expected = "SELECT * FROM persons, relatives, profile_pictures AS pictures"
        val stmt = sqlStatement {
            select {
                all()
                from {
                    +"persons"
                    +"relatives"
                    +"profile_pictures".alias("pictures")
                }
            }
        }

        stmt.queryOneliner shouldBe expected
    }

    "select query - AND, = operators" {
        val expectedQuery = "SELECT name, age, AVG(age) AS avg_age FROM persons WHERE" +
            " name = ? AND age = ? AND avg_age = ?"

        val name = "joe"
        val age = 20
        val avgAge = "25"
        val expectedParams = listOf(name, age, avgAge)

        val stmt = sqlStatement {
            select {
                +"name"
                +"age"
                +avg("age").alias("avg_age")
            }
            from {
                +"persons"
            }
            where {
                +"name".eq(name)
                +"age".eq(age)
                +"avg_age".eq(avgAge)
            }
        }

        stmt.queryOneliner shouldBe expectedQuery
        stmt.params shouldBe expectedParams
    }

    "select query - AND, OR, =, !=, >=, <= operators" {
        val expectedQuery = "SELECT name, age, AVG(age) AS avg_age FROM persons WHERE" +
            " name = ? AND age >= ? AND (name != ? OR avg_age <= ?)"

        val name = "joe"
        val age = 20
        val avgAge = "25"
        val expectedParams = listOf(name, age, name, avgAge)

        val stmt = sqlStatement {
            select {
                +"name"
                +"age"
                +avg("age").alias("avg_age")
            }
            from {
                +"persons"
            }
            where {
                +"name".eq(name)
                +"age".gte(age)
                or {
                    +"name".neq(name)
                    +"avg_age".lte(avgAge)
                }
            }
        }

        stmt.queryOneliner shouldBe expectedQuery
        stmt.params shouldBe expectedParams
    }

})
