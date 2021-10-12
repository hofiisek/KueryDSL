# SqlDSL
- A simple DSL to generate SQL code in a Kotlin idiomatic way

---

### Supports (for now):
- SELECT statement
- AVG, MIN, MAX, COUNT functions
- table & column aliases
- LEFT/RIGHT/INNER/FULL JOIN clauses
  - both USING and ON clauses
- complex conditions in WHERE clause
  - operators such as =, !=, <, >, <=, >=
  - operators IN, BETWEEN, IS NULL
  - arbitrary nesting of logical operators (AND, OR, XOR, NOT) with proper bracketing
- ORDER BY clause
- LIMIT, OFFSET clauses

---

### TO-DO
- write tests ;))
- support for GROUP BY and HAVING clauses
- review & finalize brackets design (of logical operators)
- allow to prefix columns with table aliases
- improve aliases to disallow calling basically anything on aliased column/table name
  - e.g. +"col".alias("name").gt("20")
- better query formatting

--- 
### Examples
```
// Simple SELECT statement
val stmt = sqlStatement {
    select {
        +"name"
        +avg("age").alias("avg_age")
    }
    from {
        +"persons".alias
    }
    where {
        +"age".gt(25)
    }
    orderBy {
        "name".desc()
    }
    limit(10)
    offset(5)
}
    
// generated query
SELECT name, AVG(age) AS avg_age
FROM persons 
WHERE age > ?
ORDER BY name DESC
LIMIT ?
OFFSET ?
```

```
// Still quite simple SELECT statement with nested OR condition
val stmt = sqlStatement {
    select {
        +"name"
        +max("age").alias("max_age")
    }
    from {
        +"persons"
    }
    where {
        +"age".gt(25)
        +"max_age".between(30, 50)
        or {
            +"name".eq("Joe")
            +"name".eq("Josh")
        }
    }
}

// generated query
SELECT name, MAX(age) AS max_age
FROM persons 
WHERE age > ? AND max_age BETWEEN ? AND ? AND (name = ? OR name = ?)

parameters: [25, 30, 50, Joe, Josh]
```

```
// Complex SELECT statement with joins and complex conditions
val stmt = sqlStatement {
    select {
        +"p.name"
        +"p.age"
        +"r.first_name".alias("r_first_name")
        +"r.surname".alias("r_surname")
    }
    from {
        +"persons".alias("p")
        innerJoin("relatives".alias("r")).on {
            +"r.surname".eq("p.surname")
        }
        leftJoin("surnames".alias("s")).using("id")
    }
    where {
        +"p.age".gt(25)
        or {
            +"p.name".eq("Joe")
            +"p.name".eq("Josh")
            and {
                +"r.first_name".eq("Sheldon")
                +"r.surname".eq("Cooper")
                +"r.age".between(30, 50).not()
            }
        }
    }
}

// generated query
SELECT p.name, p.age, r.first_name AS r_first_name, r.surname AS r_surname
FROM persons AS p
INNER JOIN relatives AS r ON (r.surname = p.surname)
LEFT JOIN surnames AS s USING (id)
WHERE p.age > ? AND (p.name = ? OR p.name = ? OR (r.first_name = ? AND r.surname = ? AND NOT r.age BETWEEN ? AND ?))

parameters: [25, Joe, Josh, Sheldon, Cooper, 30, 50]
```