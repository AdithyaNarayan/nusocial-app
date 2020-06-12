package com.teamnusocial.nusocial.utils

class KnuthShuffle(private val numberOfElements: Int) {
    fun shuffle(): Array<Int> {
        val result = Array<Int>(numberOfElements) {i -> i + 1}
        var n = numberOfElements
        while(n > 1) {
            val k = java.util.Random().nextInt(n--)
            val t = result[n]
            result[n] = result[k]
            result[k] = t
        }
        return result
    }
    fun getColorFromInt(id: Int): String {
        return when (id) {
            1 -> "red"
            2 -> "blue"
            else -> "white"
        }
    }
}