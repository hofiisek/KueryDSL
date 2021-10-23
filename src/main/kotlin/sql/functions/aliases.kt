package sql.functions

/**
 * @author Dominik Hoftych
 */
fun String.alias(alias: String) = NameWithAlias(this, alias)

data class NameWithAlias(val name: String, val alias: String = "") {
    override fun toString() = name + if (alias.isNotBlank()) " AS $alias" else ""
}
