package kuery.functions

sealed class Condition {
    abstract val condition: String
}

data class MultiParamCondition(override val condition: String, val params: List<Any>): Condition()
data class NoParamCondition(override val condition: String): Condition()
data class SingleParamCondition(override val condition: String, val param: Any): Condition()
