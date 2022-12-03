fun main() {
    fun getSortedCaloriesPerElf(input: List<String>): List<Int> {
        return input.fold(mutableListOf(0)) { acc, cur ->
            if (cur.isBlank()) {
                acc.add(0)
            } else {
                acc[acc.lastIndex] += cur.toInt()
            }
            acc
        }.sortedDescending()
    }

    fun part1(input: List<String>): Int {
        return getSortedCaloriesPerElf(input).first()
    }

    fun part2(input: List<String>): Int {
        return getSortedCaloriesPerElf(input)
                .subList(0, 3)
                .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
