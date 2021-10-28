package sql.statements

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import sql.functions.*
import sql.operators.OperatorType
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

    "simple select query test with AND operators" {
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

    "simple select query test with AND operator and nested OR operator" {
        val expectedQuery = "SELECT name, age, AVG(age) AS avg_age FROM persons WHERE" +
            " age >= ? AND (name != ? OR avg_age <= ?)"

        val name = "joe"
        val age = 20
        val avgAge = "25"
        val expectedParams = listOf(age, name, avgAge)

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

    "simple select query test with OR operator" {
        val expectedQuery = "SELECT name, age, AVG(age) AS avg_age FROM persons WHERE" +
            " age >= ? OR name != ? OR avg_age <= ?"

        val name = "joe"
        val age = 20
        val avgAge = "25"
        val expectedParams = listOf(age, name, avgAge)

        val stmt = sqlStatement {
            select {
                +"name"
                +"age"
                +avg("age").alias("avg_age")
            }
            from {
                +"persons"
            }
            where(operator = OperatorType.OR) {
                +"age".gte(age)
                +"name".neq(name)
                +"avg_age".lte(avgAge)
            }
        }

        stmt.queryOneliner shouldBe expectedQuery
        stmt.params shouldBe expectedParams
    }

    "complex select query test with various nested operator" {
        val expectedQuery = "SELECT name, age FROM persons WHERE " +
            "age >= ? AND name != ? " +
            "AND (name = ? OR age < ? OR (name IN(?,?) AND age BETWEEN ? AND ? AND NOT (name IS NULL XOR age IS NOT NULL)))"

        val name = "Joe"
        val otherName = "Joey"
        val age = 20
        val otherAge = 25
        val expectedParams = listOf(age, name, name, age, name, otherName, age, otherAge)

        val stmt = sqlStatement {
            select {
                +"name"
                +"age"
            }
            from {
                +"persons"
            }
            where {
                +"age".gte(age)
                +"name".neq(name)
                or {
                    +"name".eq(name)
                    +"age".lt(age)
                    and {
                        +"name".`in`(name, otherName)
                        +"age".between(age, otherAge)
                        not(operator = OperatorType.XOR) {
                            +"name".isNull()
                            +"age".isNotNull()
                        }
                    }
                }
            }
        }

        stmt.queryOneliner shouldBe expectedQuery
        stmt.params shouldBe expectedParams
    }
})
