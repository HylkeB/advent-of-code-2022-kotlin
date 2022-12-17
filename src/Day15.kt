import kotlin.math.absoluteValue

fun main() {

    data class Sensor(
        val x: Int,
        val y: Int,
        val beaconX: Int,
        val beaconY: Int,
        val rawData: String
    ) {
        val range: Int = (x - beaconX).absoluteValue + (y - beaconY).absoluteValue
    }

    fun List<String>.parseSensors(): List<Sensor> {
        return map { line ->
            // line format: Sensor at x=2, y=18: closest beacon is at x=-2, y=15
            // x=<value>, y=<value>
            val sensorData = line.substringAfter("Sensor at ").substringBefore(":")
            val (sensorX, sensorY) = with(sensorData.split(", ")) {
                val x = this[0].split("=")[1].toInt()
                val y = this[1].split("=")[1].toInt()
                x to y
            }
            val beaconData = line.substringAfter(": closest beacon is at ")
            val (beaconX, beaconY) = with(beaconData.split(", ")) {
                val x = this[0].split("=")[1].toInt()
                val y = this[1].split("=")[1].toInt()
                x to y
            }
            Sensor(sensorX, sensorY, beaconX, beaconY, line)
        }
    }

    fun List<Sensor>.getBounds(): Bounds {
        var left = Int.MAX_VALUE
        var right = Int.MIN_VALUE
        var top = Int.MAX_VALUE
        var bottom = Int.MIN_VALUE
        forEach { sensor ->
            left = minOf(left, sensor.x - sensor.range)
            right = maxOf(right, sensor.x + sensor.range)
            top = minOf(top, sensor.y - sensor.range)
            bottom = maxOf(bottom, sensor.y + sensor.range)
        }
        return Bounds(top, left, bottom, right)
    }

    fun part1(input: List<String>, rowNumber: Int): Int {
        val sensors = input.parseSensors()
                .filter {rowNumber in (it.y - it.range)..(it.y + it.range) } // filter out sensors not in range of the target row
        val bounds = sensors.getBounds()
                .copy(top = rowNumber, bottom = rowNumber) // limit grid and thus size required to only interested line
        val grid = Grid(bounds, '.')
        grid.debugInfo()

        sensors.forEach { sensor ->
            grid.trySetGridValue(sensor.x, sensor.y, 'S')
            grid.trySetGridValue(sensor.beaconX, sensor.beaconY, 'B')

            fun inRange(x: Int, y: Int): Boolean {
                val range = (sensor.x - x).absoluteValue + (sensor.y - y).absoluteValue
                return range <= sensor.range
            }

            val xRange = (sensor.x - sensor.range)..(sensor.x + sensor.range)
            xRange.forEach { x ->
                if (inRange(x, rowNumber) && grid.getGridValue(x, rowNumber) == '.') {
                    grid.trySetGridValue(x, rowNumber, '#')
                }
            }
        }

        val noBeaconPositions = (grid.bounds.left .. grid.bounds.right).count { x ->
            grid.getGridValue(x, rowNumber) == '#'
        }
        return noBeaconPositions
    }

    fun Sensor.getCoverageRange(x: Int, maxYRange: IntRange): IntRange? {
        val columnOffset = (x - this.x).absoluteValue
        if (columnOffset > range) return null
        val remainingRange = range - columnOffset
        val actualRange = (y - remainingRange)..(y + remainingRange)
        return maxOf(actualRange.first, maxYRange.first)..minOf(actualRange.last, maxYRange.last)
    }

    fun part2(input: List<String>, xRange: IntRange, yRange: IntRange): Long {

        val sensors = input.parseSensors()
        xRange.forEach { x ->
            var currentYPosition = 0
            sensors.mapNotNull { it.getCoverageRange(x, yRange) }
                    .sortedBy { it.first }
                    .forEach { coverageRange ->
                        if (currentYPosition + 1 >= coverageRange.first) {
                            currentYPosition = maxOf(currentYPosition, coverageRange.last)
                        } else {
                            println("result x: $x, y: ${currentYPosition + 1}")
                            return (x * 4000000L) + currentYPosition + 1
                        }
                    }
        }

        return 0L
    }

    fun drawFullSampleData() {
        val sensors = readInput("Day15_test").parseSensors()
        val bounds = sensors.getBounds()
        val grid = Grid(bounds, '.')
        grid.debugInfo()
        sensors.forEach { sensor ->

            val debugGrid = Grid(listOf(sensor).getBounds(), '.')
            grid.trySetGridValue(sensor.x, sensor.y, 'S')
            grid.trySetGridValue(sensor.beaconX, sensor.beaconY, 'B')
            debugGrid.trySetGridValue(sensor.x, sensor.y, 'S')
            debugGrid.trySetGridValue(sensor.beaconX, sensor.beaconY, 'B')

            fun inRange(x: Int, y: Int): Boolean {
                val range = (sensor.x - x).absoluteValue + (sensor.y - y).absoluteValue
                return range <= sensor.range
            }

            val xRange = (sensor.x - sensor.range)..(sensor.x + sensor.range)
            val yRange = (sensor.y - sensor.range)..(sensor.y + sensor.range)
            xRange.forEach { x ->
                yRange.forEach { y ->
                    if (inRange(x, y) && grid.getGridValue(x, y) == '.') {
                        grid.trySetGridValue(x, y, '#')
                        debugGrid.trySetGridValue(x, y, '#')
                    }
                }
            }
            debugGrid.debugDraw()
        }
        grid.debugInfo()
        grid.debugDraw()
    }

    // test if implementation meets criteria from the description, like:
    drawFullSampleData()
    val dayNumber = 15
    val testInput = readInput("Day${dayNumber}_test")
    val testResultPart1 = part1(testInput, 10)
    val testAnswerPart1 = 26
    check(testResultPart1 == testAnswerPart1) { "Part 1: got $testResultPart1 but expected $testAnswerPart1" }
    val testResultPart2 = part2(testInput, 0..20, 0..20)
    val testAnswerPart2 = 56000011L
    check(testResultPart2 == testAnswerPart2) { "Part 2: got $testResultPart2 but expected $testAnswerPart2" }

    val input = readInput("Day$dayNumber")
    println(part1(input, 2000000))
    println(part2(input, 0..4000000, 0..4000000))
}
