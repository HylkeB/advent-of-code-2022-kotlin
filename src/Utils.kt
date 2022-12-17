import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun String.trimUnevenIndent(): String {
    return lines().joinToString(separator = "\n") { it.trim() }
}

data class Bounds (
    val top: Int,
    val left: Int,
    val bottom: Int,
    val right: Int
)

class Grid<T: Any>(
        val bounds: Bounds,
        initialValue: T
) {
    val width = (bounds.right - bounds.left) + 1
    val height = (bounds.bottom - bounds.top) + 1

    val grid = Array(height) { Array<Any>(width) { initialValue } }


    private fun Int.normalizeX(): Int = width - ((bounds.right - this) + 1)
    private fun Int.normalizeY(): Int = height - ((bounds.bottom - this) + 1)

    fun trySetGridValue(x: Int, y: Int, value: T) {
        if (x in bounds.left .. bounds.right && y in bounds.top..bounds.bottom) {
            setGridValue(x, y, value)
        }
    }

    fun setGridValue(x: Int, y: Int, value: T) {
        grid[y.normalizeY()][x.normalizeX()] = value
    }

    fun getGridValue(x: Int, y: Int): T? {
        if (x !in bounds.left..bounds.right || y !in bounds.top..bounds.bottom) return null
        return grid[y.normalizeY()][x.normalizeX()] as T
    }

    fun debugInfo() {
        println("bounds: $bounds, width: $width, height: $height")
    }
    fun debugDraw() {
        val canvas = grid.joinToString("\n") { it.joinToString("") }
        println(canvas)
        println()
    }
}