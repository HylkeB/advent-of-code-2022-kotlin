private sealed class Instruction {
    object Noop : Instruction()
    object StartAddX : Instruction()
    class FinishAddX(val amount: Int): Instruction()
}

fun main() {

    fun List<String>.toInstructions(): List<Instruction> {
        return flatMap {
            if (it == "noop") {
                listOf(Instruction.Noop)
            } else {
                val amount = it.split(" ")[1].toInt()
                listOf(Instruction.StartAddX, Instruction.FinishAddX(amount))
            }
        }
    }

    fun Int.cycleToLineIndex(): Int {
        return (this - 1) / 40
    }

    fun Int.clockCycleToPixelPosition(): Int {
        return (this - 1) % 40
    }

    fun Int.isSpriteVisibleAt(pixelPosition: Int): Boolean {
        return pixelPosition in this - 1 .. this + 1
    }

    fun MutableList<String>.appendAtLine(lineIndex: Int, value: Char) {
        if (this.size < (1 + lineIndex)) {
            this.add("")
        }
        this[lineIndex] = this[lineIndex] + value
    }

    fun part1(input: List<String>): Int {
        fun Int.isMultipleOf40OffsetBy20(): Boolean {
            return (this - 20) % 40 == 0
        }
        val sums = mutableListOf<Int>()
        var currentClockCycle = 1
        var xRegisterValue = 1
        input.forEach {
            if (it == "noop") {
                if (currentClockCycle.isMultipleOf40OffsetBy20()) {
                    sums += currentClockCycle * xRegisterValue
                }
                currentClockCycle++
            } else {
                if (currentClockCycle.isMultipleOf40OffsetBy20()) {
                    sums += currentClockCycle * xRegisterValue
                }
                currentClockCycle++
                if (currentClockCycle.isMultipleOf40OffsetBy20()) {
                    sums += currentClockCycle * xRegisterValue
                }
                currentClockCycle++
                xRegisterValue += it.split(" ")[1].toInt()
            }
        }
        return sums.sum()
    }

    fun part2(input: List<String>): String {
        var currentClockCycle = 1
        var currentSpritePosition = 1
        val lines = mutableListOf<String>()
        input.toInstructions()
                .forEach { instruction ->
                    // First draw
                    val pixelPosition = currentClockCycle.clockCycleToPixelPosition()
                    val isSpriteVisible = currentSpritePosition.isSpriteVisibleAt(pixelPosition)
                    val characterToDraw = if (isSpriteVisible) '#' else '.'
                    lines.appendAtLine(currentClockCycle.cycleToLineIndex(), characterToDraw)

                    // Then perform instruction
                    if (instruction is Instruction.FinishAddX) {
                        currentSpritePosition += instruction.amount
                    }
                    currentClockCycle++
                }
        return lines.joinToString(separator = System.lineSeparator())
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    check(part2(testInput) == """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
    """.trimIndent())

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
