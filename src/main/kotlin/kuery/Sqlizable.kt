package kuery
/**
 * @author Dominik Hoftych
 */
interface Sqlizable {
    fun toSqlOneliner(): String
    fun toSqlFormatted(): String = toSqlOneliner()
}
