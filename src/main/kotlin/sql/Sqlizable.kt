package sql
/**
 * @author Dominik Hoftych
 */
interface Sqlizable {
    fun toSqlFormatted(): String = toSqlOneliner()
    fun toSqlOneliner(): String
}
