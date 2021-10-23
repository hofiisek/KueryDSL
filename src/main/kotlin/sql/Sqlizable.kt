package sql
/**
 * @author Dominik Hoftych
 */
interface Sqlizable {
    fun toSql(): String
}

