fun main() {
    val rocks = listOf(
        """
            ..@@@@.
            .......
            .......
            .......
        """.trimIndent(),
        """
            ...@...
            ..@@@..
            ...@...
            .......
            .......
            .......
        """.trimIndent(),
        """
            ....@..
            ....@..
            ..@@@..
            .......
            .......
            .......
        """.trimIndent(),
        """
            ..@....
            ..@....
            ..@....
            ..@....
            .......
            .......
            .......
        """.trimIndent(),
        """
            ..@@...
            ..@@...
            .......
            .......
            .......
        """.trimIndent()
    )

    fun part1(input: List<String>): Int {
        val directions = input[0]
        var bucket = """
            #######
        """.trimIndent()

        fun addRock(rockIndex: Int) {
            val rock = rocks[rockIndex]
            bucket = rock + "\n" + bucket
        }
        fun moveRockLeft() {
            // check if left move is possible
            bucket.lines().forEach { line ->
                line.fold('#') { charLeft, currentChar ->
                    if (currentChar == '@' && charLeft == '#') return
                    currentChar
                }
            }

            val newBucket = bucket.lines().map { line ->
                if (line.contains("@")) {
                    line.fold("") { acc, currentChar ->
                        if (currentChar == '@' && acc.last() == '.') {
                            acc.dropLast(1) + currentChar + '.'
                        } else {
                            acc + currentChar
                        }
                    }
                } else {
                    line
                }
            }
            bucket = newBucket.joinToString(separator = "\n")
        }
        fun moveRockRight() {
            // check if right move is possible
            bucket.lines().forEach { line ->
                line.foldRight('#') { currentChar, charRight ->
                    if (currentChar == '@' && charRight == '#') return
                    currentChar
                }
            }

            val newBucket = bucket.lines().map { line ->
                if (line.contains("@")) {
                    line.foldRight("") { currentChar, acc ->
                        if (currentChar == '@' && acc.first() == '.') {
                            "." + currentChar + acc.drop(1)
                        } else {
                            currentChar + acc
                        }
                    }
                } else {
                    line
                }
            }

            bucket = newBucket.joinToString(separator = "\n")
        }
        fun moveRockDown(): Boolean {
            // Check if down move is possible
            bucket.lines().foldRight("#######") { lineToConsider, lineBelow ->
                lineToConsider.forEachIndexed { index, char ->
                    if (char == '@' && lineBelow[index] == '#') return false
                }
                lineToConsider
            }

            val newBucket = bucket.lines().foldRight(emptyList<String>()) { lineToMoveDown, acc ->
                if (lineToMoveDown.contains('@')) {
                    val bottomLine = acc.first()
                    // change current bottom line
                    val newBottomLine = bottomLine.mapIndexed { index, c ->
                        if (lineToMoveDown[index] == '@') '@' else c
                    }.joinToString(separator = "")
                    val newLineToMoveDown = lineToMoveDown.replace('@', '.')
                    listOf(newLineToMoveDown, newBottomLine) + acc.drop(1)
                } else {
                    listOf(lineToMoveDown) + acc
                }
            }

            bucket = newBucket.joinToString(separator = "\n")
            return true
        }
        fun trimTopSpace() {
            bucket = bucket.lines().filterNot { line -> line.all { it == '.' } }
                    .joinToString(separator = "\n")
        }
        fun makeStationary() {
            bucket = bucket.replace('@', '#')
        }

        var currentRockIndex = 0
        var currentDirectionIndex = 0

        repeat(2022) {
            addRock(currentRockIndex)
//            println()
//            println("rock $it")
//            println(bucket.lines().dropLast(1).joinToString(separator = "\n") + "\n-------")
            currentRockIndex = (currentRockIndex + 1) % rocks.size
            do {
                val moveDirection = directions[currentDirectionIndex]
                currentDirectionIndex = (currentDirectionIndex + 1) % directions.length
                when (moveDirection) {
                    '<' -> moveRockLeft()
                    '>' -> moveRockRight()
                    else -> throw IllegalArgumentException("Unknown move direction $moveDirection")
                }
            } while (moveRockDown())
            makeStationary()
            trimTopSpace()
        }
        println()
        println(bucket.lines().dropLast(1).joinToString(separator = "\n") + "\n-------")

        return bucket.lines().size - 1
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val dayNumber = 17
    val testInput = readInput("Day${dayNumber}_test")
    val testResultPart1 = part1(testInput)
    val testAnswerPart1 = 3068
    check(testResultPart1 == testAnswerPart1) { "Part 1: got $testResultPart1 but expected $testAnswerPart1" }
    val testResultPart2 = part2(testInput)
    val testAnswerPart2 = 12345
//    check(testResultPart2 == testAnswerPart2) { "Part 2: got $testResultPart2 but expected $testAnswerPart2" }

    val input = readInput("Day$dayNumber")
    println(part1(input))
    println(part2(input))
}
