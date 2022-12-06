// not very efficient but it works for this assignment
private fun List<Any>.isUnique(): Boolean {
    this.forEachIndexed { indexOuter, valueOuter ->
        this.forEachIndexed { indexInner, valueInner ->
            if (indexInner != indexOuter && valueInner == valueOuter) return false
        }
    }
    return true
}

private fun String.firstIndexAfterUniqueStreamOfCharacters(size: Int): Int {
    val buffer = ArrayDeque<Char>()
    this.forEachIndexed { index, cur ->
        if (buffer.size < size) {
            buffer.addFirst(cur)
            return@forEachIndexed
        }

        if (buffer.isUnique()) {
            return index
        } else {
            buffer.addFirst(cur)
            buffer.removeLast()
        }
    }
    throw IllegalArgumentException("No unique stream found")
}

fun main() {
    fun part1(input: List<String>): List<Int> {
        return input.map { signal ->
            signal.firstIndexAfterUniqueStreamOfCharacters(4)
        }
    }

    fun part2(input: List<String>): List<Int> {
        return input.map { signal ->
            signal.firstIndexAfterUniqueStreamOfCharacters(14)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == listOf(7, 5, 6, 10, 11))
    check(part2(testInput) == listOf(19, 23, 23, 29, 26))

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
