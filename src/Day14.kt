import java.lang.IllegalArgumentException

fun main() {

    class Scan(
            private val top: Int,
            private val left: Int,
            private val bottom: Int,
            private val right: Int,
    ) {

        private val width = (right - left) + 1
        private val height = (bottom - top) + 1

        private val grid = Array(height) { Array(width) { '.' } }

        init {
            setGridValue(0, 0, '+')
        }

        private fun Int.normalizeX(): Int = width - ((right - this) + 1)
        private fun Int.normalizeY(): Int = top + this

        private fun setGridValue(x: Int, y: Int, value: Char) {
            grid[y.normalizeY()][x.normalizeX()] = value
        }

        private fun getGridValue(x: Int, y: Int): Char? {
            if (x !in left..right || y !in top..bottom) return null
            return grid[y.normalizeY()][x.normalizeX()]
        }

        fun addRock(x: Int, y: Int) {
            setGridValue(x, y, '#')
        }

        fun simulateSand(): Boolean {
            tailrec fun simulate(x: Int, y: Int): Boolean {
                val oneDown = getGridValue(x, y + 1) ?: return false // outside canvas
                if (oneDown == '.') return simulate(x, y + 1)

                val downLeft = getGridValue(x - 1, y + 1) ?: return false // outside canvas
                if (downLeft == '.') return simulate(x - 1, y + 1)

                val downRight = getGridValue(x + 1, y + 1) ?: return false // outside canvas
                if (downRight == '.') return simulate(x + 1, y + 1)

                setGridValue(x, y, 'O')
                return true

            }

            return simulate(0, 0)
        }

        fun debugDraw() {
            val canvas = grid.joinToString("\n") { it.joinToString("") }
            println(canvas)
        }
    }

    // top left, bottom right
    fun List<List<Pair<Int, Int>>>.getBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        var left = Int.MAX_VALUE
        var right = Int.MIN_VALUE
        var top = 0
        var bottom = Int.MIN_VALUE
        forEach { scanLine ->
            scanLine.forEach { (x, y) ->
                left = minOf(left, x)
                right = maxOf(right, x)
                top = minOf(top, y)
                bottom = maxOf(bottom, y)
            }
        }
        return (top to left) to (bottom to right)
    }

    fun List<String>.parseInput(): Scan {
        val scanLines = map { line ->
            line.split(" -> ")
                    .map {
                        val splitted = it.split(",")
                        splitted[0].toInt() to splitted[1].toInt()
                    }
                    .map { (x, y) -> x - 500 to y } // normalize
        }
        val (topLeft, bottomRight) = scanLines.getBounds()
        val (top, left) = topLeft
        val (bottom, right) = bottomRight
        val scan = Scan(top, left, bottom, right)
        scanLines.forEach { scanLine ->
            scanLine.forEachIndexed { index, (curX, curY) ->
                if (index > 0) {
                    val (prevX, prevY) = scanLine[index - 1]
                    if (curX == prevX) { // vertical line
                        val minY = minOf(curY, prevY)
                        val maxY = maxOf(curY, prevY)
                        val yRange = minY..maxY
                        val x = curX
                        yRange.forEach { y ->
                            scan.addRock(x, y)
                        }

                    } else if (curY == prevY) {
                        val minX = minOf(curX, prevX)
                        val maxX = maxOf(curX, prevX)
                        val xRange = minX..maxX
                        val y = curY
                        xRange.forEach { x ->
                            scan.addRock(x, y)
                        }

                    } else {
                        throw IllegalArgumentException("No support for diagonal lines")
                    }
                }
            }
        }
        return scan
    }

    fun part1(input: List<String>): Int {
        val scan = input.parseInput()
        var counter = 0
        while (scan.simulateSand()) {
            counter++
        }
        return counter
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val dayNumber = 14
    val testInput = readInput("Day${dayNumber}_test")
    val testResultPart1 = part1(testInput)
    check(testResultPart1 == 24) { "was $testResultPart1" }
//    check(part2(testInput) == 12345)

    val input = readInput("Day$dayNumber")
    println(part1(input))
    println(part2(input))
}
