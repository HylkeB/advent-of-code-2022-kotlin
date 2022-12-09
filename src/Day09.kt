import kotlin.math.absoluteValue

private data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position): Position {
        return Position(x + other.x, y + other.y)
    }

    operator fun minus(other: Position): Position {
        return Position(x - other.x, y - other.y)
    }

    infix fun moveTo(target: Position): Position {
        val delta = this - target
        return if (delta.x.absoluteValue == 2 || delta.y.absoluteValue == 2) {
            Position(
                this.x - delta.x.coerceIn(-1, 1),
                this.y - delta.y.coerceIn(-1, 1)
            )
        } else {
            this.copy()
        }
    }
}

private sealed class Direction(val deltaPosition: Position) {
    class Up : Direction(Position(0, 1))
    class Down : Direction(Position(0, -1))
    class Left : Direction(Position(-1, 0))
    class Right : Direction(Position(1, 0))
}

private fun String.toDirection(): Direction {
    return when (this) {
        "U" -> Direction.Up()
        "D" -> Direction.Down()
        "L" -> Direction.Left()
        "R" -> Direction.Right()
        else -> throw IllegalArgumentException("Unknown direction: $this")
    }
}

fun main() {

    fun parseLine(line: String): Pair<Direction, Int> {
        return line.split(" ").let { it[0].toDirection() to it[1].toInt() }
    }

    fun part1(input: List<String>): Int {
        var currentHeadPosition = Position(0, 0)
        var currentTailPosition = Position(0, 0)
        val visitedPositions = mutableListOf(currentTailPosition)

        input.map { parseLine(it) }
            .forEach { (direction, amount) ->
                repeat(amount) {
                    currentHeadPosition += direction.deltaPosition
                    currentTailPosition = currentTailPosition moveTo currentHeadPosition
                    visitedPositions += currentTailPosition
                }
            }

        return visitedPositions.distinct().count()
    }

    fun part2(input: List<String>): Int {
        val positions = Array(10) { Position(0, 0) }
        val visitedPositions = mutableListOf(positions.last())

        input.map { parseLine(it) }
                .forEach { (direction, amount) ->
                    repeat(amount) {
                        positions[0] += direction.deltaPosition
                        for (positionIndex in 1..9) {
                            positions[positionIndex] = positions[positionIndex] moveTo positions[positionIndex - 1]
                        }
                        visitedPositions += positions.last()
                    }
                }

        return visitedPositions.distinct().count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val testInput2 = readInput("Day09_test2")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
