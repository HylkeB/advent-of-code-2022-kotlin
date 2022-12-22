@file:Suppress("PackageDirectoryMismatch")
package day22

import readInput

private sealed class Instruction {
    class Move(val amount: Int) : Instruction()
    class Rotate(val direction: Char) : Instruction()
}

fun main() {

    fun part1(input: List<String>): Int {
        val width = input.maxOf { it.length } // amount of columns, x position
        val height = input.size - 2 // amount of rows, y position
        val grid = Array(width) { Array(height) { ' ' } }
        repeat(height) { y ->
            val row = input[y]
            row.forEachIndexed { x, cell ->
                grid[x][y] = cell
            }
        }
        val instructions = input.last().fold(mutableListOf("")) { acc, cur ->
            if (cur.isDigit()) {
                val lastElement = acc.removeLast()
                acc += (lastElement + cur)
            } else {
                acc += listOf(cur.toString(), "")
            }

            acc
        }.filter {
            it.isNotEmpty()
        }.map { it ->
            if (it.length == 1 && !it.first().isDigit()) {
                Instruction.Rotate(it.first())
            } else {
                Instruction.Move(it.toInt())
            }
        }

        tailrec fun tryMoveX(currentX: Int, currentY: Int, xDirection: Int): Int? {
            var normalizedXPosition = (currentX + xDirection) % width
            if (normalizedXPosition < 0) {
                normalizedXPosition += width
            }
            return when (grid[normalizedXPosition][currentY]) {
                ' ' -> tryMoveX(normalizedXPosition, currentY, xDirection)
                '#' -> null
                else /* '.' */ -> normalizedXPosition
            }
        }

        tailrec fun tryMoveY(currentX: Int, currentY: Int, yDirection: Int): Int? {
            var normalizedYPosition = (currentY + yDirection) % height
            if (normalizedYPosition < 0) {
                normalizedYPosition += height
            }
            return when (grid[currentX][normalizedYPosition]) {
                ' ' -> tryMoveY(currentX, normalizedYPosition, yDirection)
                '#' -> null
                else /* '.' */ -> normalizedYPosition
            }
        }


        var currentX = input.first().indexOfFirst { it == '.' }
        var currentY = 0
        var currentXDirection = 1
        var currentYDirection = 0

        instructions.forEach { instruction ->
            when (instruction) {
                is Instruction.Move -> {
                    repeat(instruction.amount) {
                        if (currentXDirection != 0) {
                            val nextX = tryMoveX(currentX, currentY, currentXDirection) ?: return@forEach
                            currentX = nextX
                        } else {
                            val nextY = tryMoveY(currentX, currentY, currentYDirection) ?: return@forEach
                            currentY = nextY
                        }
                    }
                }
                is Instruction.Rotate -> {
                    if (instruction.direction == 'L') {
                        //      > .. ^ .. < .. v
                        // x:   1 .. 0 ..-1 .. 0
                        // y:   0 ..-1 .. 0 .. 1
                        // next x = cur y
                        // next y = inverse cur x
                        val nextX = currentYDirection
                        val nextY = currentXDirection * -1
                        currentXDirection = nextX
                        currentYDirection = nextY
                    } else { // 'R'
                        //      > .. v .. < .. ^
                        // x:   1 .. 0 ..-1 .. 0
                        // y:   0 .. 1 .. 0 ..-1
                        // next x = inverse cur y
                        // next y = cur x
                        val nextX = currentYDirection * -1
                        val nextY = currentXDirection
                        currentXDirection = nextX
                        currentYDirection = nextY
                    }
                }
            }
        }

        val directionValue = when {
            currentXDirection == 1 -> 0
            currentYDirection == 1 -> 1
            currentXDirection == -1 -> 2
            currentYDirection == -1 -> 3
            else -> throw IllegalStateException("Unknown direction: $currentXDirection, $currentYDirection")
        }

        return 1000 * (currentY + 1) + 4 * (currentX + 1) + directionValue
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val dayNumber = 22
    val testInput = readInput("Day${dayNumber}_test")
    val testResultPart1 = part1(testInput)
    val testAnswerPart1 = 6032
    check(testResultPart1 == testAnswerPart1) { "Part 1: got $testResultPart1 but expected $testAnswerPart1" }
    val testResultPart2 = part2(testInput)
    val testAnswerPart2 = 12345
//    check(testResultPart2 == testAnswerPart2) { "Part 2: got $testResultPart2 but expected $testAnswerPart2" }

    val input = readInput("Day$dayNumber")
    println(part1(input))
    println(part2(input))
}
