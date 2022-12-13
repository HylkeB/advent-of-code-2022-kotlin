import java.util.PriorityQueue
import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun main() {

    data class Position(val x: Int, val y: Int) {

        fun manhattanDistanceTo(other: Position): Int {
            return (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue
        }

        fun crowDistanceTo(other: Position): Double {
            val delta = this - other
            return sqrt((delta.x * delta.x + delta.y * delta.y).toDouble())
        }

        operator fun minus(other: Position): Position {
            return Position(x - other.x, y - other.y)
        }
    }

    class GridInfo(val grid: Array<Array<Int>>, val start: Position, val end: Position, val amountOfRows: Int, val amountOfColumns: Int)

    fun getEdgePositions(gridInfo: GridInfo, currentPosition: Position, previousPositions: List<Position>): List<Position> {
        val leftPosition = currentPosition.copy(x = currentPosition.x - 1)
        val rightPosition = currentPosition.copy(x = currentPosition.x + 1)
        val topPosition = currentPosition.copy(y = currentPosition.y - 1)
        val bottomPosition = currentPosition.copy(y = currentPosition.y + 1)
        val maxHeight = gridInfo.grid[currentPosition.x][currentPosition.y] + 1
        val heightRange = gridInfo.grid[currentPosition.x][currentPosition.y] .. maxHeight
        val edges = mutableListOf<Position>()

        if (leftPosition.x in 0 until gridInfo.amountOfColumns && gridInfo.grid[leftPosition.x][leftPosition.y] in heightRange) {
            edges.add(leftPosition)
        }

        if (rightPosition.x in 0 until gridInfo.amountOfColumns && gridInfo.grid[rightPosition.x][rightPosition.y] in heightRange) {
            edges.add(rightPosition)
        }

        if (topPosition.y in 0 until gridInfo.amountOfRows && gridInfo.grid[topPosition.x][topPosition.y] in heightRange) {
            edges.add(topPosition)
        }

        if (bottomPosition.y in 0 until gridInfo.amountOfRows && gridInfo.grid[bottomPosition.x][bottomPosition.y] in heightRange) {
            edges.add(bottomPosition)
        }
        return edges.filterNot { it in previousPositions }
    }

    fun getEdgePositionsReversed(gridInfo: GridInfo, currentPosition: Position, previousPositions: List<Position>): List<Position> {
        val leftPosition = currentPosition.copy(x = currentPosition.x - 1)
        val rightPosition = currentPosition.copy(x = currentPosition.x + 1)
        val topPosition = currentPosition.copy(y = currentPosition.y - 1)
        val bottomPosition = currentPosition.copy(y = currentPosition.y + 1)
        val currentHeight = gridInfo.grid[currentPosition.x][currentPosition.y]
        // if current is j, minheight is h
        val minHeight = currentHeight - 1
        val maxHeight = if (currentHeight == 'h' - 'a') {
            'j' - 'a'
        } else {
            currentHeight
        }
//        val minHeight = currentHeight - 1 // if it is 4 or lower, it should stay 4 or lower, otherwise it should be 0

        val heightRange = minHeight .. maxHeight
        val edges = mutableListOf<Position>()

        if (leftPosition.x in 0 until gridInfo.amountOfColumns && gridInfo.grid[leftPosition.x][leftPosition.y] in heightRange) {
            edges.add(leftPosition)
        }

        if (rightPosition.x in 0 until gridInfo.amountOfColumns && gridInfo.grid[rightPosition.x][rightPosition.y] in heightRange) {
            edges.add(rightPosition)
        }

        if (topPosition.y in 0 until gridInfo.amountOfRows && gridInfo.grid[topPosition.x][topPosition.y] in heightRange) {
            edges.add(topPosition)
        }

        if (bottomPosition.y in 0 until gridInfo.amountOfRows && gridInfo.grid[bottomPosition.x][bottomPosition.y] in heightRange) {
            edges.add(bottomPosition)
        }
        return edges.filterNot { it in previousPositions }
    }

    class Path(
            val visitedPositions: List<Position>,
            val nextPosition: Position,
            val lengthToEndAsTheCrowFlies: Double,
            val manhattanLength: Int
    ) {
        fun crowHeuristic(): Double {
            return visitedPositions.size + lengthToEndAsTheCrowFlies
        }

        fun manhattanHeuristic(): Int {
            return visitedPositions.size + manhattanLength
        }
    }

    fun parseInput(input: List<String>): GridInfo {
        val amountOfRows = input.size
        val amountOfColumns = input[0].length
        val grid = Array(amountOfColumns) { Array(amountOfRows) { 0 } }
        lateinit var start: Position
        lateinit var end: Position
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                val height = when (char) {
                    'S' -> {
                        start = Position(x, y)
                        0
                    }

                    'E' -> {
                        end = Position(x, y)
                        25
                    }

                    else -> char - 'a'
                }
                grid[x][y] = height
            }
        }
//        grid.forEach {
//            println(it.joinToString(","))
//        }
        return GridInfo(grid, start, end, amountOfRows, amountOfColumns)
    }

    fun printDebugInfo(path: List<Position>, edges: List<Position>, gridInfo: GridInfo, targetStart: Position? = null) {
        val directions = path.mapIndexed { index, position ->
            val previousPosition = if (index == 0) {
                return@mapIndexed "E"
            } else {
                path[index - 1]
            }
            when {
                previousPosition.x < position.x -> "<"
                previousPosition.x > position.x -> ">"
                previousPosition.y < position.y -> "^"
                previousPosition.y > position.y -> "v"
                else -> throw IllegalStateException("Same position in path")
            }
        }
        var info = ""
        repeat(gridInfo.amountOfRows) { y ->
            var rowInfo = ""
            repeat(gridInfo.amountOfColumns) { x ->
                val positionToConsider = Position(x, y)
                val indexOfPath = path.indexOf(positionToConsider)
                rowInfo += if (indexOfPath != -1) {
                    directions[indexOfPath]
                } else if (edges.contains(positionToConsider)) {
                    "?"
                } else if (positionToConsider == targetStart) {
                    "S"
                } else if (gridInfo.grid[x][y] == 0) {
                    "s"
                } else {
                    "."
                }
            }
            info += rowInfo + "\n"
        }
        check(info.count { it == '?' } == edges.size)
        println(info)
    }

    fun part1(input: List<String>): Int {
        val gridInfo = parseInput(input)
        val priorityQueue = PriorityQueue<Path>(compareBy { it.crowHeuristic() })
                .apply {
                    add(Path(emptyList(), gridInfo.start, gridInfo.start.crowDistanceTo(gridInfo.end), gridInfo.start.manhattanDistanceTo(gridInfo.end)))
                }

        var i = 0
        while (priorityQueue.peek().nextPosition != gridInfo.end) {
            val currentPQSize = priorityQueue.size
            val currentPath = priorityQueue.remove()
            val visitedPositions = currentPath.visitedPositions + currentPath.nextPosition
            val edges = getEdgePositions(gridInfo, currentPath.nextPosition, currentPath.visitedPositions)
            edges.forEach { nextPosition ->
                val pathToAdd = Path(visitedPositions, nextPosition, nextPosition.crowDistanceTo(gridInfo.end), nextPosition.manhattanDistanceTo(gridInfo.end))
                val contestingPath = priorityQueue.find { it.nextPosition == nextPosition }
                if (contestingPath == null) {
                    priorityQueue.add(pathToAdd)
                } else {
                    if (pathToAdd.crowHeuristic() < contestingPath.crowHeuristic()) {
                        priorityQueue.add(pathToAdd)
                        priorityQueue.remove(contestingPath)
                    }
                }
//                priorityQueue.add(Path(visitedPositions, nextPosition, nextPosition.crowDistanceTo(gridInfo.end), nextPosition.manhattanDistanceTo(gridInfo.end)))
            }
//            println("step $i: current heuristic: ${currentPath.crowHeuristic()}, worst heuristic: ${}")
            println("Step $i: current heuristic: ${currentPath.crowHeuristic()}, " +
                    "visited: ${currentPath.visitedPositions.size}, " +
                    "crowLength: ${currentPath.lengthToEndAsTheCrowFlies}, " +
                    "nextPosition: ${currentPath.nextPosition}, " +
                    "closestLowSpot: ${gridInfo.end}, " +
                    "start pq size: $currentPQSize, adding ${edges.size} new options")
            printDebugInfo(visitedPositions, edges, gridInfo, gridInfo.end)
            i++
        }

        return priorityQueue.peek().visitedPositions.size
    }

    fun part1Reversed(input: List<String>): Int {
        val gridInfo = parseInput(input)
//        val priorityQueue = PriorityQueue<Path> { left, right -> left.crowHeuristic().compareTo(right.crowHeuristic()) }
        val priorityQueue = PriorityQueue<Path>(compareBy { it.crowHeuristic() })
                .apply {
                    add(Path(emptyList(), gridInfo.end, gridInfo.end.crowDistanceTo(gridInfo.start), gridInfo.end.manhattanDistanceTo(gridInfo.start)))
                }

        var i = 0
        var addCounter = 0
        var replaceCounter = 0
        var keepCounter = 0
        while (priorityQueue.peek().nextPosition != gridInfo.start) {
            val currentPQSize = priorityQueue.size
            val currentPath = priorityQueue.remove()
            val visitedPositions = currentPath.visitedPositions + currentPath.nextPosition
            val edges = getEdgePositionsReversed(gridInfo, currentPath.nextPosition, currentPath.visitedPositions)
//            if (i % 30 == 0) {
//                printDebugInfo(visitedPositions, edges, gridInfo)
//            }
            edges.forEach { nextPosition ->
                val pathToAdd = Path(visitedPositions, nextPosition, nextPosition.crowDistanceTo(gridInfo.start), nextPosition.manhattanDistanceTo(gridInfo.start))
                val contestingPath = priorityQueue.find { it.visitedPositions.contains(nextPosition) || it.nextPosition == nextPosition }
                if (contestingPath == null) {
                    priorityQueue.add(pathToAdd)
                    addCounter++
                } else {
                    if (pathToAdd.crowHeuristic() < contestingPath.crowHeuristic()) {
                        priorityQueue.add(pathToAdd)
                        priorityQueue.remove(contestingPath)
                        replaceCounter++
                    } else {
                        keepCounter++
                    }
                }
            }
//            println("Step $i: current heuristic: ${currentPath.crowHeuristic()}, start pq size: $currentPQSize, adding ${edges.size} new options")
            i++
        }

        println("Optimizations:: add=$addCounter, replace=$replaceCounter, keep=$keepCounter")

        return priorityQueue.peek().visitedPositions.size
    }

    fun GridInfo.findClosestLowSpot(from: Position): Position {
        var currentDistance = Double.MAX_VALUE
        lateinit var currentPosition: Position
        grid.forEachIndexed { x, column ->
            column.forEachIndexed { y, height ->
                if (height == 0) {
                    val positionToConsider = Position(x, y)
                    val distanceToConsider = positionToConsider.crowDistanceTo(from)
//                    println("Considering $positionToConsider with distance $distanceToConsider")
                    if (currentDistance > distanceToConsider) {
                        currentDistance = distanceToConsider
                        currentPosition = positionToConsider
                    }
                }
            }
        }
//        println("closest low spot from $from is $currentPosition at distance $currentDistance")
        return currentPosition
    }

    fun part2(input: List<String>): Int {
        // reversed approach; heuristic is find the closest 0 height spot
        val gridInfo = parseInput(input)
        val priorityQueue = PriorityQueue<Path>(compareBy { it.crowHeuristic() })
                .apply {
                    add(Path(emptyList(), gridInfo.end, gridInfo.end.crowDistanceTo(gridInfo.findClosestLowSpot(gridInfo.end)), gridInfo.end.manhattanDistanceTo(gridInfo.findClosestLowSpot(gridInfo.end))))
                }

        var i = 0
        var addCounter = 0
        var replaceCounter = 0
        var keepCounter = 0
        while (priorityQueue.peek().nextPosition.let { gridInfo.grid[it.x][it.y] != 0 }) {
            val currentPQSize = priorityQueue.size
            val currentPath = priorityQueue.remove()
            val visitedPositions = currentPath.visitedPositions + currentPath.nextPosition
            val edges = getEdgePositionsReversed(gridInfo, currentPath.nextPosition, currentPath.visitedPositions)
//            if (i % 30 == 0) {
//            }
            edges.forEach { nextPosition ->
                val pathToAdd = Path(visitedPositions, nextPosition, nextPosition.crowDistanceTo(gridInfo.findClosestLowSpot(nextPosition)), nextPosition.manhattanDistanceTo(gridInfo.findClosestLowSpot(nextPosition)))
                val contestingPath = priorityQueue.find { it.visitedPositions.contains(nextPosition) || it.nextPosition == nextPosition }
                if (contestingPath == null) {
                    priorityQueue.add(pathToAdd)
                    addCounter++
                } else {
                    if (pathToAdd.crowHeuristic() < contestingPath.crowHeuristic()) {
                        priorityQueue.add(pathToAdd)
                        priorityQueue.remove(contestingPath)
                        replaceCounter++
                    } else {
                        keepCounter++
                    }
                }
            }
            val currentLowSpot = gridInfo.findClosestLowSpot(currentPath.nextPosition)
            println("Step $i: current heuristic: ${currentPath.crowHeuristic()}, " +
                    "visited: ${currentPath.visitedPositions.size}, " +
                    "crowLength: ${currentPath.lengthToEndAsTheCrowFlies}, " +
                    "nextPosition: ${currentPath.nextPosition}, " +
                    "closestLowSpot: $currentLowSpot, " +
                    "start pq size: $currentPQSize, adding ${edges.size} new options")
            printDebugInfo(visitedPositions, edges, gridInfo, currentLowSpot)
            i++
        }

        println("Optimizations:: add=$addCounter, replace=$replaceCounter, keep=$keepCounter")

        val targetPath = priorityQueue.peek()
        printDebugInfo(targetPath.visitedPositions + targetPath.nextPosition, emptyList(), gridInfo)

        return priorityQueue.peek().visitedPositions.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
//    check(part1(testInput) == 31)
//    check(part1Reversed(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
//    println(part1(input))
//    println(part1Reversed(input))
    println(part2(input))
}
