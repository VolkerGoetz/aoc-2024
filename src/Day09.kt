class Disk(val diskMap: String) {

    sealed class DiskBlock(
        val start: Int,
        val size: Int,
    )

    class FileBlock(start: Int, size: Int, val fileId: Int) : DiskBlock(start, size) {
        override fun toString(): String {
            return "FileBlock(start=$start, size=$size, fileId=$fileId)"
        }
    }

    class EmptyBlock(start: Int, size: Int) : DiskBlock(start, size) {
        override fun toString(): String {
            return "EmptyBlock(size=$size)"
        }
    }

    val blockUsage = buildList {
        var fileId = 0
        var currentBlock = 0
        diskMap.forEachIndexed { index, ch ->
            val size = ch.toString().toInt()
            val b = if (index % 2 == 0) FileBlock(currentBlock, size, fileId++) else EmptyBlock(currentBlock, size)
            (1..size).forEach { add(b) }
            currentBlock += size
        }
    }.toMutableList()

    fun Int.fileIdStr(): String =
        when (this) {
            in 0..9 -> this.toString()
            in 10..70 -> Char(this - 9 + 65).toString()
            else -> "*"
        }

    override fun toString(): String {
        return blockUsage.joinToString(separator = "") { b ->
            when (b) {
                is FileBlock -> b.fileId.fileIdStr()
                is EmptyBlock -> "."
            }
        }
    }

    fun <T> MutableList<T>.swap(i1: Int, i2: Int) {
        val tmp = this[i1]
        this[i1] = this[i2]
        this[i2] = tmp
    }

    fun checksum(): Long =
        blockUsage.mapIndexed { index, block ->
            when (block) {
                is FileBlock -> (index * block.fileId).toLong()
                else -> 0L
            }
        }.sum()


    fun reorderBlocks() {

        var emptyBlockIndices = blockUsage.mapIndexed { index, block -> index to block }.filter { it.second is EmptyBlock }.map { it.first }
        var diskBlocksIndices = blockUsage.mapIndexed { index, block -> index to block }.filter { it.second is FileBlock }.map { it.first }

        emptyBlockIndices.zip(diskBlocksIndices.reversed())
            .filter { pair -> pair.first < pair.second }
            .forEach { pair -> blockUsage.swap(pair.first, pair.second) }
    }

    fun reorderFiles() {
        var freeSpaces = blockUsage
            .filter { block -> block is EmptyBlock }
            .toMutableSet()

        // Files
        blockUsage
            .filter { block -> block is FileBlock }
            .distinct()
            .sortedByDescending { block -> (block as FileBlock).fileId }
            .forEach { it ->
                val f = it as FileBlock
                freeSpaces
                    .filter { it -> it.start < f.start && it.size >= f.size }
                    .minByOrNull { block -> block.start }
                    ?.let { freeChunk ->
                        for (i in 0..f.size - 1) {
                            blockUsage.swap(freeChunk.start + i, f.start + i)
                        }

                        freeSpaces.remove(freeChunk)
                        if (freeChunk.size > f.size) {
                            freeSpaces.add(EmptyBlock(freeChunk.start + f.size, freeChunk.size - f.size))
                        }
                    }
            }
    }
}

fun main() {

    fun part1(input: List<String>): Long {

        val disk = Disk(input.first())
        println(disk)
        disk.reorderBlocks()
        println(disk)
        return disk.checksum()
    }

    fun part2(input: List<String>): Long {
        val disk = Disk(input.first())
        //println(disk)
        disk.reorderFiles()
        //println(disk)
        return disk.checksum()
    }

    check(part1(listOf("12345")).also { it.println() } == 60L)
    check(part2(listOf("12345")).also { it.println() } == 132L)

    val testInput = readInput("Day09_test")
    check(testInput.size == 1)
    check(part1(testInput).also { it.println() } == 1928L)
    check(part2(testInput).also { it.println() } == 2858L)

    val input = readInput("Day09")
    check(input.size == 1)
    part1(input).println()
    part2(input).println()
}
