fun main() {

    class Operation(
        val leftValue: String,
        val operator: String,
        val rightValue: String,
    ) {
        fun execute(oldValue: Long): Long {
            val left = if (leftValue == "old") oldValue else leftValue.toLong()
            val right = if (rightValue == "old") oldValue else rightValue.toLong()
            return when (operator) {
                "*" -> left * right
                "+" -> left + right
                else -> throw IllegalStateException("Unknown operator: $operator")
            }
        }
    }

    class Monkey {
        val items = ArrayDeque<Long>()
        lateinit var operation: Operation
        var testModulo = -1L
        var testTrue = -1
        var testFalse = -1
        var inspectCounter = 0L
    }

    fun parseMonkeys(input: List<String>): List<Monkey> {
        val monkeys = mutableListOf<Monkey>()
        input.map { it.trim() }.forEach { line ->
            when {
                line.startsWith("Monkey") -> {
                    monkeys.add(Monkey())
                }
                line.startsWith("Starting items") -> {
                    line.substringAfter("Starting items: ").split(", ").map { it.toLong() }
                            .forEach {
                                monkeys.last().items.addLast(it)
                            }
                }
                line.startsWith("Operation") -> {
                    val operationData = line.substringAfter("Operation: new = ").split(" ")
                    monkeys.last().operation = Operation(operationData[0], operationData[1], operationData[2])
                }
                line.startsWith("Test") -> {
                    monkeys.last().testModulo = line.substringAfter("Test: divisible by ").toLong()
                }
                line.startsWith("If true") -> {
                    monkeys.last().testTrue = line.substringAfter("If true: throw to monkey ").toInt()
                }
                line.startsWith("If false") -> {
                    monkeys.last().testFalse = line.substringAfter("If false: throw to monkey ").toInt()
                }
            }
        }
        return monkeys
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        repeat(20) {
            monkeys.forEach { monkey ->
                while (monkey.items.isNotEmpty()) {
                    val startWorryScore = monkey.items.removeFirst()
                    val inspectWorryScore = monkey.operation.execute(startWorryScore)
                    monkey.inspectCounter++
                    val boredomWorryScore = inspectWorryScore / 3
                    val test = boredomWorryScore % monkey.testModulo
                    if (test == 0L) {
                        monkeys[monkey.testTrue].items.addLast(boredomWorryScore)
                    } else {
                        monkeys[monkey.testFalse].items.addLast(boredomWorryScore)
                    }
                }
            }
        }
        val busiestMonkeys = monkeys.sortedByDescending { it.inspectCounter }
                .take(2)

        return busiestMonkeys[0].inspectCounter * busiestMonkeys[1].inspectCounter
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        val biggestCommonDenominator = monkeys.fold(1L) { acc, monkey ->
            acc * monkey.testModulo
        }
        repeat(10000) {
            monkeys.forEachIndexed { index, monkey ->
                while (monkey.items.isNotEmpty()) {
                    val startWorryScore = monkey.items.removeFirst()
                    val inspectWorryScore = monkey.operation.execute(startWorryScore)
                    val normalizedInspectorWorryScore = inspectWorryScore % biggestCommonDenominator
                    monkey.inspectCounter++
                    val test = normalizedInspectorWorryScore % monkey.testModulo
                    val newMonkey = if (test == 0L) {
                        monkey.testTrue
                    } else {
                        monkey.testFalse
                    }
                    monkeys[newMonkey].items.addLast(normalizedInspectorWorryScore)
                }
            }
        }
        val busiestMonkeys = monkeys.sortedByDescending { it.inspectCounter }

        return busiestMonkeys[0].inspectCounter * busiestMonkeys[1].inspectCounter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
