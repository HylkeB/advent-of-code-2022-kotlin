private sealed class File {
    abstract val name: String
    abstract val size: Long
    abstract val parentDirectory: Directory
}

private class Directory(
    override val name: String,
    private val parentDirectoryOrNull: Directory?
) : File() {
    override val parentDirectory: Directory
        get() = parentDirectoryOrNull!!
    val files: MutableList<File> = mutableListOf()
    override val size get() = files.sumOf { it.size }

    override fun toString(): String {
        return """
            $name (dir)
            ${files.joinToString("\n") { it.toString() }}
        """.trimUnevenIndent()
    }
}

private class LeafFile(
    override val name: String,
    override val size: Long,
    override val parentDirectory: Directory
): File() {
    override fun toString(): String {
        return "$name (file, size=$size)"
    }
}

private fun String.isCDInDirection(): Boolean = startsWith("$ cd") && !isCDOutDirection()
private fun String.isCDOutDirection(): Boolean = this == "$ cd .."
private fun String.isListInstruction(): Boolean = startsWith("$ ls")
private fun String.parseFile(parentDirectory: Directory): File {
    val parts = split(" ")
    val name = parts[1]
    return if (parts[0] == "dir") {
        Directory(name, parentDirectory)
    } else {
        val size = parts[0].toLong()
        LeafFile(name, size, parentDirectory)
    }
}

private fun getDirectoryStructure(input: List<String>): Directory {
    val outerDirectory = Directory("/", null)
    var currentDirectory: Directory = outerDirectory

    input.drop(1) // skip first entry since that changes the directory to the outer directory
            .forEach {
                if (it.isCDInDirection()) {
                    val directoryName = it.split(" ").last()
                    currentDirectory = currentDirectory.files.first { it.name == directoryName } as Directory
                    return@forEach
                }

                if (it.isCDOutDirection()) {
                    currentDirectory = currentDirectory.parentDirectory
                    return@forEach
                }

                if (it.isListInstruction()) return@forEach

                val file = it.parseFile(currentDirectory)
                currentDirectory.files.add(file)
            }

    return outerDirectory
}

fun main() {
    fun part1(input: List<String>): Long {
        val rootDirectory = getDirectoryStructure(input)

        var totalSize = 0L
        fun loopThrough(directory: Directory) {
            val directorySize = directory.size
            if (directorySize <= 100000) {
                // println("Add size for directory ${directory.name} ($directorySize)")
                totalSize += directorySize
            }
            directory.files.forEach {
                if (it is Directory) {
                    loopThrough(it)
                }
            }
        }
        loopThrough(rootDirectory)
        return totalSize
    }

    fun part2(input: List<String>): Long {
        val rootDirectory = getDirectoryStructure(input)
        val totalSpace = 70000000L
        val requiredSpace = 30000000L
        val usedSpace = rootDirectory.size
        val unusedSpace = totalSpace - usedSpace
        val missingSpace = requiredSpace - unusedSpace

        var sizeOfDirectoryToDelete = Long.MAX_VALUE
        fun loopThrough(directory: Directory) {
            val directorySize = directory.size
            if (directorySize >= missingSpace) {
                if (sizeOfDirectoryToDelete > directorySize) {
                    // println("Updated directory to remove: ${directory.name} (frees $directorySize size)")
                    sizeOfDirectoryToDelete = directorySize
                }
            }
            directory.files.forEach {
                if (it is Directory) {
                    loopThrough(it)
                }
            }
        }
        loopThrough(rootDirectory)

        return sizeOfDirectoryToDelete
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
