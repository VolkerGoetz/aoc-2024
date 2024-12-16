fun main() {
    fun CharGrid.solve(): Pair<Int, List<Set<Point>>> {

        val maze = this

        data class TracePoint(
            val point: Point,
            val direction: Direction,
        )

        data class QueueEntry(
            val trcpt: TracePoint,
            val cost: Int,
            val pathPoints: Set<Point>
        )

        val startPoint = maze.allPoints.first { maze.get(it) == 'S' }
        val startTrcPt = TracePoint(startPoint, Direction.East)

        val queue = mutableListOf(QueueEntry(startTrcPt, 0, setOf(startPoint)))
        val seen = mutableSetOf<TracePoint>()
        var minCost = Int.MAX_VALUE
        val validPathSets = mutableListOf<Set<Point>>()

        while (queue.isNotEmpty()) {
            val qentry = queue.removeFirst()
            val place = qentry.trcpt.point
            seen += qentry.trcpt
            if (maze[place] == 'E') {
                if (minCost > qentry.cost) {
                    minCost = qentry.cost
                    validPathSets.clear()
                }
                if (minCost == qentry.cost) {
                    validPathSets.add(qentry.pathPoints)
                }
            } else {
                queue.addAll(
                    listOf(
                        QueueEntry(
                            TracePoint(place + qentry.trcpt.direction, qentry.trcpt.direction),
                            qentry.cost + 1,
                            qentry.pathPoints + (place + qentry.trcpt.direction),
                        ),
                        QueueEntry(
                            TracePoint(place, qentry.trcpt.direction.turnLeft()),
                            qentry.cost + 1000,
                            qentry.pathPoints,
                        ),
                        QueueEntry(
                            TracePoint(place, qentry.trcpt.direction.turnRight()),
                            qentry.cost + 1000,
                            qentry.pathPoints,
                        ),
                    ).filter {
                        it.trcpt !in seen && maze[it.trcpt.point] != '#'
                    }
                ).let {
                    //re-sort only if changed
                    if (it) queue.sortBy { it.cost }
                }
            }
        }

        return minCost to validPathSets
    }

    fun CharGrid.printGrid() {
        for (y in 0..<gridSize.y) {
            for (x in 0..<gridSize.x) {
                print(get(x to y))
            }
            kotlin.io.println()
        }
    }

    fun part1(input: List<String>) = CharGrid(input)
        .also { it.printGrid() }
        .solve()
        .also { println("Min cost = ${it.first} with ${it.second.size} paths") }
        .also { it.second.println() }
        .first.toLong()

    fun part2(input: List<String>) = CharGrid(input)
        .also { it.printGrid() }
        .solve()
        .also { println("Min cost = ${it.first} with ${it.second.size} paths") }
        .second.flatMap { it }.distinct().size.toLong()

    val testInputVg1 = readInput("Day16_test_vg_1")
    check(testInputVg1.size == 3)
    check(part1(testInputVg1).also { it.println() } == 12L)

    val testInputVg2 = readInput("Day16_test_vg_2")
    check(testInputVg2.size == 5)
    check(part1(testInputVg2).also { it.println() } == 3005L)

    val testInput1 = readInput("Day16_test_1")
    check(testInput1.size == 15)
    check(part1(testInput1).also { it.println() } == 7036L)
    check(part2(testInput1).also { it.println() } == 45L)

    val testInput2 = readInput("Day16_test_2")
    check(testInput2.size == 17)
    check(part1(testInput2).also { it.println() } == 11048L)
    check(part2(testInput2).also { it.println() } == 64L)

    val input = readInput("Day16")
    check(input.size == 141)
    //part1(input).println()
    check(part1(input).also { it.println() } == 114476L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 508L)
}
