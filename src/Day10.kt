fun main() {

    val directions = setOf(
        Direction(0, -1), // up
        Direction(0, 1), // down
        Direction(-1, 0), // left
        Direction(1, 0), // right
    )

    fun IntGrid.walkPath(start: Point, prevLevel: Int = -1, path: List<Point> = listOf(start)): Set<List<Point>> {

        val currentLevel = get(start)

        if (currentLevel != prevLevel + 1) return emptySet()
        if (currentLevel == 9) return setOf(path)
        return directions
            .map { d -> start + d }
            .filter { it in this }
            .flatMap {
                walkPath(it, currentLevel, path + it)
            }
            .toSet()
    }

    fun part1(input: List<String>): Long {

        val map = IntGrid(input)
        val startingPoints = map.allPoints.filter { it -> map.get(it) == 0 }.sorted()
        println("startingPoints: $startingPoints")
        val res = startingPoints.sumOf { p ->
            map.walkPath(p)
                .distinctBy { it.last() }
                .size
        }

        return res.toLong()
    }

    fun part2(input: List<String>): Long {
        val map = IntGrid(input)
        val startingPoints = map.allPoints.filter { it -> map.get(it) == 0 }.sorted()
        println("startingPoints: $startingPoints")
        val res = startingPoints.flatMap { p -> map.walkPath(p) }
        return res.size.toLong()
    }

    check(
        part1(
            listOf(
                "0123",
                "1234",
                "8765",
                "9876"
            )
        ).also { it.println() } == 1L)

    val testInput1 = readInput("Day10_test_1")
    check(testInput1.size == 7)
    check(part1(testInput1).also { it.println() } == 2L)

    val testInput = readInput("Day10_test")
    check(testInput.size == 8)
    check(part1(testInput).also { it.println() } == 36L)
    check(part2(testInput).also { it.println() } == 81L)

    val input = readInput("Day10")
    check(input.size == 40)
    //part1(input).println()
    check(part1(input).also { it.println() } == 461L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 875L)
}
