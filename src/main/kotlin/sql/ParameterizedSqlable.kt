package sql

/**
 * @author Dominik Hoftych
 */
abstract class ParameterizedSqlable : Sqlable {
    val params: MutableList<Any?> = mutableListOf()
}
