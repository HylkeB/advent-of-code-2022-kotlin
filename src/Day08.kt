fun main() {
    tailrec fun isVisible(
            grid: Array<Array<Int>>,
            rowIndex: Int,
            columnIndex: Int,
            treeHeight: Int,
            xDirection: Int,
            yDirection: Int
    ): Boolean {
        require(xDirection != 0 || yDirection != 0) { "Only one direction can be used" }
        val rowIndexToConsider = rowIndex + xDirection
        val columnIndexToConsider = columnIndex + yDirection

        fun isOutsideGrid(): Boolean {
            return rowIndexToConsider < 0 || rowIndexToConsider >= grid[0].size || columnIndexToConsider < 0 || columnIndexToConsider >= grid.size
        }

        return if (isOutsideGrid()) {
            true
        } else {
            val adjacentHeight = grid[rowIndexToConsider][columnIndexToConsider]
            if (treeHeight <= adjacentHeight) {
                false
            } else {
                isVisible(grid, rowIndexToConsider, columnIndexToConsider, treeHeight, xDirection, yDirection)
            }
        }
    }

    tailrec fun countVisibleTrees(
            grid: Array<Array<Int>>,
            rowIndex: Int,
            columnIndex: Int,
            treeHeight: Int,
            xDirection: Int,
            yDirection: Int,
            visitedTrees: Int = 0
    ): Int {
        require(xDirection != 0 || yDirection != 0) { "Only one direction can be used" }
        val rowIndexToConsider = rowIndex + xDirection
        val columnIndexToConsider = columnIndex + yDirection

        fun isOutsideGrid(): Boolean {
            return rowIndexToConsider < 0 || rowIndexToConsider >= grid[0].size || columnIndexToConsider < 0 || columnIndexToConsider >= grid.size
        }

        return if (isOutsideGrid()) {
            visitedTrees
        } else {
            val adjacentHeight = grid[rowIndexToConsider][columnIndexToConsider]
            if (treeHeight <= adjacentHeight) {
                visitedTrees + 1
            } else {
                countVisibleTrees(grid, rowIndexToConsider, columnIndexToConsider, treeHeight, xDirection, yDirection, visitedTrees + 1)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val height = input.size
        val width = input[0].length
        val treeGrid: Array<Array<Int>> = Array(height) { Array(width) { 0 } }
        input.forEachIndexed { rowIndex, rowData ->
            rowData.map { it.digitToInt() }.forEachIndexed { columnIndex, height ->
                treeGrid[rowIndex][columnIndex] = height
            }
        }
        var amountOfVisibleTrees = 0
        treeGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, height ->
                val isVisibleLeft = isVisible(treeGrid, rowIndex, columnIndex, height, -1, 0)
                val isVisibleRight = isVisible(treeGrid, rowIndex, columnIndex, height, 1, 0)
                val isVisibleTop = isVisible(treeGrid, rowIndex, columnIndex, height, 0, -1)
                val isVisibleBottom  = isVisible(treeGrid, rowIndex, columnIndex, height, 0, 1)
                if (isVisibleLeft || isVisibleRight || isVisibleTop || isVisibleBottom) {
                    amountOfVisibleTrees++
                }
            }
        }
        return amountOfVisibleTrees
    }

    fun part2(input: List<String>): Int {

        val height = input.size
        val width = input[0].length
        val treeGrid: Array<Array<Int>> = Array(height) { Array(width) { 0 } }
        input.forEachIndexed { rowIndex, rowData ->
            rowData.map { it.digitToInt() }.forEachIndexed { columnIndex, height ->
                treeGrid[rowIndex][columnIndex] = height
            }
        }
        var maxScore = 0
        treeGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, height ->
                val scoreLeft = countVisibleTrees(treeGrid, rowIndex, columnIndex, height, -1, 0)
                val scoreRight = countVisibleTrees(treeGrid, rowIndex, columnIndex, height, 1, 0)
                val scoreTop = countVisibleTrees(treeGrid, rowIndex, columnIndex, height, 0, -1)
                val scoreBottom  = countVisibleTrees(treeGrid, rowIndex, columnIndex, height, 0, 1)
                val score = scoreLeft * scoreRight * scoreTop * scoreBottom
                maxScore = maxOf(score, maxScore)
            }
        }
        return maxScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
