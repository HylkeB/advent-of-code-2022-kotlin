private sealed class Value {
    class List : Value() {
        var values = emptyList<Value>()
    }
    class Primitive(val value: Int) : Value()
}

private sealed class ValidationResult
private object Valid : ValidationResult()
private object Invalid : ValidationResult()
private object Unknown : ValidationResult()

fun main() {
    fun parseValue(string: String): Value {
        return if (string.startsWith("[")) {
            val content = string
                    .substringAfter("[")
                    .substringBeforeLast("]")
                    .fold(mutableListOf("")) { acc, char ->
                        if (char == ',' && acc.last().count { it == '[' } == acc.last().count { it == ']' }) {
                            acc.add("")
                        } else {
                            acc.add(acc.removeLast() + char)
                        }
                        acc
                    }
                    .mapNotNull {
                        if (it.isEmpty()) {
                            null
                        } else {
                            parseValue(it)
                        }
                    }

            Value.List().apply {
                this.values = content
            }
        } else {
            Value.Primitive(string.toInt())
        }
    }

    fun parsePairs(input: List<String>): List<Pair<Value, Value>> {
        val pairs = mutableListOf<Pair<Value, Value>>()
        repeat(input.size) { index ->
            if ((index - 1) % 3 == 0) {
                val left = input[index - 1]
                val right = input[index]
                pairs.add(parseValue(left) to parseValue(right))
            }
        }
        return pairs
    }

    fun parseSignals(input: List<String>): List<Value> {
        return input.filter { it.isNotEmpty() }
                .map {
                    parseValue(it)
                }
    }


    fun compare(left: Value, right: Value): ValidationResult {
        if (left is Value.Primitive && right is Value.Primitive) {
            return if (left.value < right.value) {
                Valid
            } else if (left.value > right.value) {
                Invalid
            } else {
                Unknown
            }
        }

        // make sure both are lists
        val leftAsList = when (left) {
            is Value.List -> left
            is Value.Primitive -> Value.List().apply { values = listOf(left) }
        }

        val rightAsList = when (right) {
            is Value.List -> right
            is Value.Primitive -> Value.List().apply { values = listOf(right) }
        }

        var currentIndex = 0
        while (true) {
            val leftItem = leftAsList.values.getOrNull(currentIndex)
            val rightItem = rightAsList.values.getOrNull(currentIndex)
            if (leftItem == null && rightItem == null) {
                return Unknown
            } else if (leftItem == null) (
                    return Valid
                    ) else if (rightItem == null) {
                return Invalid
            } else {
                val value = compare(leftItem, rightItem)
                if (value != Unknown) {
                    return value
                } else {
                    currentIndex++
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val pairs = parsePairs(input)
        val validatedPairs = pairs.map {
            compare(it.first, it.second)
        }
                .mapIndexed { index, value ->
                    when (value) {
                        Invalid -> false to index + 1
                        Valid -> true to index + 1
                        Unknown -> throw IllegalStateException("Unknown validation")
                    }
                }
        return validatedPairs.filter { it.first }.sumOf { it.second }
    }

    fun part2(input: List<String>): Int {
        val signals = parseSignals(input)
        val dividerSignals = listOf(parseValue("[[2]]"), parseValue("[[6]]"))
        val allSignals = signals + dividerSignals
        val sorted = allSignals.sortedWith { left, right ->
            when (compare(left, right)) {
                Valid -> -1
                Invalid -> 1
                Unknown -> throw IllegalStateException("Unknown validation result")
            }
        }
        return (sorted.indexOfFirst { it in dividerSignals } + 1) * (sorted.indexOfLast { it in dividerSignals } + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
