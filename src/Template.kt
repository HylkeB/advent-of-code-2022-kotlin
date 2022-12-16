fun main() {
    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val dayNumber = 123456
    val testInput = readInput("Day${dayNumber}_test")
    check(part1(testInput) == 12345)
//    check(part2(testInput) == 12345)

    val input = readInput("Day$dayNumber")
    println(part1(input))
    println(part2(input))
}
