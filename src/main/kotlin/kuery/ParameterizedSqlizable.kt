package kuery

/**
 * @author Dominik Hoftych
 */
abstract class ParameterizedSqlizable : Sqlizable {
    val params: MutableList<Any?> = mutableListOf()
}
