fun main() {
    fun getElfRangePairs(input: List<String>): List<Pair<IntRange, IntRange>> {
        return input.map { pair ->
            val parts = pair.split(",")
            val elf1 = parts[0].split("-")
            val elf2 = parts[1].split("-")
            val rangeElf1 = elf1[0].toInt() .. elf1[1].toInt()
            val rangeElf2 = elf2[0].toInt() .. elf2[1].toInt()
            rangeElf1 to rangeElf2
        }
    }

    fun part1(input: List<String>): Int {
        return getElfRangePairs(input)
                .count { (rangeElf1, rangeElf2) ->
                    rangeElf1.all { it in rangeElf2 } || rangeElf2.all { it in rangeElf1 }
                }
    }

    fun part2(input: List<String>): Int {
        return getElfRangePairs(input)
                .count { (rangeElf1, rangeElf2) ->
                    rangeElf1.any { it in rangeElf2 } || rangeElf2.any { it in rangeElf1 }
                }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
