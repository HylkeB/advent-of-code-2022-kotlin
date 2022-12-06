import java.lang.IllegalArgumentException
fun main() {

    fun calculatePriorityOfItem(item: Char): Int {
        return when (item) {
            in 'a'..'z' -> 1 + (item - 'a')
            in 'A'..'Z' -> 27 + (item - 'A')
            else -> throw IllegalArgumentException("Unknown item: $item")
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { backpack ->
            val allItems = backpack.toList()
            val leftCompartment = allItems.subList(0, allItems.size / 2)
            val rightCompartment = allItems.subList(allItems.size / 2, allItems.size)
            val misplacedItem = leftCompartment.first { rightCompartment.contains(it) }
            calculatePriorityOfItem(misplacedItem)
        }
    }

    fun part2(input: List<String>): Int {
        val groupsOfElfBackpacks = input.fold(mutableListOf(mutableListOf<String>())) { acc, cur ->
            if (acc.last().size == 3) {
                acc.add(mutableListOf())
            }
            acc.last().add(cur)
            acc
        }

        return groupsOfElfBackpacks.sumOf { group ->
            val badge = group[0].first {
                group[1].contains(it) && group[2].contains(it)
            }
            calculatePriorityOfItem(badge)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
