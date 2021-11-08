package kuery.functions

/**
 * @author Dominik Hoftych
 */
fun String.alias(alias: String) = NameWithAlias(this, alias)

data class NameWithAlias(private val name: String, private val alias: String = "") {
    override fun toString() = name + if (alias.isNotBlank()) " AS $alias" else ""
}
