package sql

/**
 * @author Dominik Hoftych
 */
interface Sqlable {
    fun toSql(): String
}
