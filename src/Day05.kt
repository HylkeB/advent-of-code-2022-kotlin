
private class Stack(val index: Int, val items: ArrayDeque<Char>) {
    override fun toString() = "$index: ${items.reversed()}"
}

private class MoveInstruction(val amount: Int, val fromIndex: Int, val toIndex: Int) {
    override fun toString(): String = "move $amount from $fromIndex to $toIndex"
}

private fun parseData(input: List<String>): Pair<List<Stack>, List<MoveInstruction>> {
    val startingStackData = input.subList(0, input.indexOf("") - 1)
    val amountOfStacks = input[input.indexOf("") - 1].last().digitToInt()
    val stacks = List(amountOfStacks) { index -> Stack(index, ArrayDeque()) }
    startingStackData.forEach { stackData ->
        stackData.forEachIndexed { index, item ->
            if (item in 'A'..'Z') {
                // index 1, 5, 9 equal index 0, 1, 2, so divide by 4
                val stackIndex = (index / 4)
                stacks[stackIndex].items.addLast(item)
            }
        }
    }

    val moveInstructionData = input.subList(input.indexOf("") + 1, input.size)
    val moveInstructions = moveInstructionData.map { moveData ->
        val parts = moveData.split(" ")
        // minus 1 is because we work with 0 indexed information
        MoveInstruction(parts[1].toInt(), parts[3].toInt() - 1, parts[5].toInt() - 1)
    }
    return stacks to moveInstructions
}

fun main() {
    fun part1(input: List<String>): String {
        val (stacks, moveInstructions) = parseData(input)
        moveInstructions.forEach { instruction ->
            repeat(instruction.amount) {
                val itemMoved = stacks[instruction.fromIndex].items.removeFirst()
                stacks[instruction.toIndex].items.addFirst(itemMoved)
            }
        }
        return stacks.joinToString(separator = "") { it.items.first().toString() }
    }

    fun part2(input: List<String>): String {
        val (stacks, moveInstructions) = parseData(input)
        moveInstructions.forEach { instruction ->
            val fromStack = stacks[instruction.fromIndex].items
            val toStack = stacks[instruction.toIndex].items
            val itemsMoved = (0 until instruction.amount).map { fromStack.removeFirst() }.reversed()
            itemsMoved.forEach { itemMoved ->
                toStack.addFirst(itemMoved)
            }
        }
        return stacks.joinToString(separator = "") { it.items.first().toString() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
