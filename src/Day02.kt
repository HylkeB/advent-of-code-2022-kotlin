import java.lang.IllegalArgumentException

sealed class Hand(val pickScore: Int) {
    protected abstract val winsVersus: Hand
    private val losesVersus: Hand get() = winsVersus.winsVersus
    fun winsVersus(other: Hand): Boolean {
        return winsVersus == other
    }

    fun getLosingHand(): Hand {
        return winsVersus
    }

    fun getWinningHand(): Hand {
        return losesVersus
    }
}
object Rock : Hand(pickScore = 1) { override val winsVersus = Scissors }
object Paper : Hand(pickScore = 2) { override val winsVersus = Rock }
object Scissors : Hand(pickScore = 3) { override val winsVersus = Paper }

fun main() {

    fun decodeHand(char: Char): Hand {
        return when (char) {
            in listOf('A', 'X') -> Rock
            in listOf('B', 'Y') -> Paper
            in listOf('C', 'Z') -> Scissors
            else -> throw IllegalArgumentException("Unknown hand: $char")
        }
    }

    // Rock - Paper - Scissors
    // A - B - C
    // X - Y - Z
    // 1 - 2 - 3
    fun calculateGameScore(opponent: Hand, myself: Hand): Int {
        return when {
            opponent.winsVersus(myself) -> 0
            myself.winsVersus(opponent) -> 6
            else -> 3
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { game ->
            val opponent = decodeHand(game[0])
            val myself = decodeHand(game[2])
            calculateGameScore(opponent, myself) + myself.pickScore
        }
    }

    // x = lose, y = draw, z = win
    fun part2(input: List<String>): Int {
        return input.sumOf { game ->
            val opponent = decodeHand(game[0])
            val myself = when (game[2]) {
                'X' -> opponent.getLosingHand()
                'Y' -> opponent
                'Z' -> opponent.getWinningHand()
                else -> throw IllegalArgumentException("Unknown outcome: ${game[2]}")
            }
            calculateGameScore(opponent, myself) + myself.pickScore
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
