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
    val testResultPart1 = part1(testInput)
    val testAnswerPart1 = 12345
    check(testResultPart1 == testAnswerPart1) { "Part 1: got $testResultPart1 but expected $testAnswerPart1" }
    val testResultPart2 = part2(testInput)
    val testAnswerPart2 = 12345
//    check(testResultPart2 == testAnswerPart2) { "Part 2: got $testResultPart2 but expected $testAnswerPart2" }

    val input = readInput("Day$dayNumber")
    println(part1(input))
    println(part2(input))
}
