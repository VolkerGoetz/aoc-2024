import java.util.*

fun main() {

    data class TracePoint(
        val point: Point,
        val direction: Direction,
    )

    fun TracePoint.turnLeft() = TracePoint(point, direction.turnLeft())
    fun TracePoint.turnRight() = TracePoint(point, direction.turnRight())
    fun TracePoint.moveForward() = TracePoint(point + direction, direction)

    data class QueueEntry(
        val trcpt: TracePoint,
        val cost: Int,
    )

    class ComputerMemoryMaze(
        val size: Point = 71 to 71,
        input: List<String>,
        amount: Int,
    ) {
        val start = 0 to 0
        val end = size.x - 1 to size.y - 1
        val usedPositions = input.take(amount)
            .map { it.substringBefore(',').toInt() to it.substringAfter(',').toInt() }

        fun printGrid() {
            println("ComputerMemoryMaze size $size")
            for (y in 0..<size.y) {
                for (x in 0..<size.x) {
                    print(if (x to y in usedPositions) '#' else '.')
                }
                kotlin.io.println()
            }
        }

        operator fun contains(point: Point) = point.x in 0..end.x && point.y in 0..end.y

        fun shortestPathLength(): Int {
            val queue = PriorityQueue<QueueEntry>(compareBy { it.cost })
                .apply { add(QueueEntry(TracePoint(start, Direction.East), 0)) }
            val seen = mutableSetOf<TracePoint>()

            while (queue.isNotEmpty()) {
                val entry = queue.remove()
                seen += entry.trcpt
                if (entry.trcpt.point == end) return entry.cost
                queue.addAll(
                    listOf(
                        QueueEntry(entry.trcpt.moveForward(), entry.cost + 1),
                        QueueEntry(entry.trcpt.turnLeft(), entry.cost),
                        QueueEntry(entry.trcpt.turnRight(), entry.cost),
                    )
                        .filter { it.trcpt.point in this }
                        .filter { it.trcpt.point !in usedPositions }
                        .filter { it.trcpt !in seen }
                )
            }

            return -1
        }
    }

    fun part1(input: List<String>, memorySize: Int = 71, inputSize: Int = 1024) =
        ComputerMemoryMaze(memorySize to memorySize, input, inputSize)
            .also { it.printGrid() }
            .shortestPathLength().toLong()

    fun part2(input: List<String>, memorySize: Int = 71): String {
        var goodSize = 1
        var badSize = input.size
        var currentSize = badSize / 2

        while (badSize - goodSize != 1) {
            if (ComputerMemoryMaze(memorySize to memorySize, input, currentSize).shortestPathLength() > 0) {
                goodSize = currentSize
                currentSize += (badSize - goodSize) / 2
            } else {
                badSize = currentSize
                currentSize -= (badSize - goodSize) / 2
            }
        }
        return input.get(badSize - 1)
    }

    val testInput = readInput("Day18_test")
    check(testInput.size == 25)
    check(part1(testInput, 7, 12).also { it.println() } == 22L)
    check(part2(testInput, 7).also { it.println() } == "6,1")

    val input = readInput("Day18")
    check(input.size == 3450)
    //part1(input).println()
    check(part1(input).also { it.println() } == 264L)
    //part2(input).println()
    check(part2(input).also { it.println() } == "41,26")
}
