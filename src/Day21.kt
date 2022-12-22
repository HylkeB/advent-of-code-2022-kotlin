
private sealed class Monkey(val name: String) {
    abstract var value: Long?
    val dependingMonkeys = mutableListOf<DependingMonkey>()

    class LeafMonkey(
        name: String,
        override var value: Long?
    ) : Monkey(name)

    class DependingMonkey(
        name: String,
        val leftMonkeyName: String,
        val rightMonkeyName: String,
        val operation: Char
    ): Monkey(name) {
        lateinit var leftMonkey: Monkey
        lateinit var rightMonkey: Monkey
        override var value: Long? = null
    }
}

fun <T> T.deepFlatMap(mapper: (T) -> List<T>): List<T> {
    val result = mutableListOf(this)
    mapper(this).forEach {
        result += it.deepFlatMap(mapper)
    }
    return result
}

fun main() {

    fun List<String>.toMonkeyMap(): Map<String, Monkey> {
        val monkeyList = map {
            val name = it.substringBefore(":")
            val data = it.substringAfter(": ")
            if (data.contains(" ")) {
                val parts = data.split(" ")
                val leftMonkey = parts[0]
                val rightMonkey = parts[2]
                val operation = parts[1].first()
                Monkey.DependingMonkey(name, leftMonkey, rightMonkey, operation)
            } else {
                Monkey.LeafMonkey(name, data.toLong())
            }
        }
        val monkeyMap = monkeyList.associateBy { it.name }
        monkeyList.forEach {
            if (it is Monkey.DependingMonkey) {
                it.leftMonkey = monkeyMap[it.leftMonkeyName]!!
                it.rightMonkey = monkeyMap[it.rightMonkeyName]!!
            }
        }

        do {
            monkeyList.forEach { monkey ->
                if (monkey is Monkey.DependingMonkey && monkey.value == null) {
                    val leftValue = monkeyMap[monkey.leftMonkeyName]?.value ?: return@forEach
                    val rightValue = monkeyMap[monkey.rightMonkeyName]?.value ?: return@forEach
                    monkey.value = when (monkey.operation) {
                        '+' -> leftValue + rightValue
                        '-' -> leftValue - rightValue
                        '*' -> leftValue * rightValue
                        '/' -> leftValue / rightValue
                        else -> throw IllegalArgumentException("Unknown operation ${monkey.operation}")
                    }
                }
            }
        } while (monkeyList.any { it.value == null })

        monkeyList.forEach { monkey ->
            if (monkey is Monkey.DependingMonkey) {
                monkey.leftMonkey.dependingMonkeys.add(monkey)
                monkey.rightMonkey.dependingMonkeys.add(monkey)
            }
        }

        return monkeyMap
    }

    fun part1(input: List<String>): Long {
        val monkeyMap = input.toMonkeyMap()
        return monkeyMap["root"]!!.value!!
    }

    fun part2(input: List<String>): Long {
        val monkeyMap = input.toMonkeyMap()
        val human = monkeyMap["humn"]!!
        val allDependingMonkeys = human.deepFlatMap {
            it.dependingMonkeys
        }
        val root = monkeyMap["root"] as Monkey.DependingMonkey
        val leftIsDependentOnHuman = allDependingMonkeys.contains(root.leftMonkey)
        val rightIsDependentOnHuman = allDependingMonkeys.contains(root.rightMonkey)
        println("amount of monkeys depending directly on human: ${allDependingMonkeys.size}")
        println("left is dependent on human: $leftIsDependentOnHuman")
        println("right is dependent on human: $rightIsDependentOnHuman")

        fun Monkey.monkeyShouldBeValue(value: Long) {
            if (this == human) {
                this.value = value
                return
            }

            if (this !is Monkey.DependingMonkey) throw IllegalArgumentException("can only consider depending monkeys")

            val (targetValue, monkeyToChange) = if (allDependingMonkeys.contains(leftMonkey)) {
                // left monkey needs to change, right monkey is static
                val rightValue = rightMonkey.value!!
                // value = left <operation> right
                // value = left + right
                // 10 = 6 + 4
                // 6 = 10 - 4
                // value = left * right
                // 10 = 2 * 5
                // 2 = 10 / 5
                // value = left / right
                // 2 = 10 / 5
                // 10 = 2 * 5
                val targetValue = when (operation) {
                    '-' -> value + rightValue // value = left - right; left = value + right
                    '+' -> value - rightValue // value = left + right; left = value - right
                    '*' -> value / rightValue // value = left * right; left = value / right
                    '/' -> value * rightValue // value = left / right; left = value * right
                    else -> throw IllegalArgumentException("Unknown operation: $operation")
                }
                targetValue to leftMonkey
            } else {
                // right monkey needs to change, left monkey is static
                val leftValue = leftMonkey.value!!
                // value = left <operation> right

                // value = left - right
                // 4 = 10 - 6
                // right = left - value
                // 6 = 10 - 4

                // value = left + right
                // 10 = 6 + 4
                // right = value - left
                // 4 = 10 - 6

                // value = left * right
                // 10 = 2 * 5
                // right = value / left
                // 5 = 10 / 2

                // value = left / right
                // 2 = 10 / 5
                // right = left / value
                // 5 = 10 / 2
                val targetValue = when (operation) {
                    '-' -> leftValue - value
                    '+' -> value - leftValue
                    '*' -> value / leftValue
                    '/' -> leftValue / value
                    else -> throw IllegalArgumentException("Unknown operation: $operation")
                }
                targetValue to rightMonkey
            }
            monkeyToChange.monkeyShouldBeValue(targetValue)
        }

        val (monkeyWithTargetValue, monkeyThatNeedsToChangeValue) = if (leftIsDependentOnHuman) {
            root.rightMonkey to root.leftMonkey
        } else {
            root.leftMonkey to root.rightMonkey
        }

        monkeyThatNeedsToChangeValue.monkeyShouldBeValue(monkeyWithTargetValue.value!!)

        return human.value!!
    }

    // test if implementation meets criteria from the description, like:
    val dayNumber = 21
    val testInput = readInput("Day${dayNumber}_test")
    val testResultPart1 = part1(testInput)
    val testAnswerPart1 = 152L
    check(testResultPart1 == testAnswerPart1) { "Part 1: got $testResultPart1 but expected $testAnswerPart1" }
    val testResultPart2 = part2(testInput)
    val testAnswerPart2 = 301L
    check(testResultPart2 == testAnswerPart2) { "Part 2: got $testResultPart2 but expected $testAnswerPart2" }

    val input = readInput("Day$dayNumber")
    println(part1(input))
    println(part2(input))
}
