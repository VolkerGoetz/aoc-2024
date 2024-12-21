fun main() {

    fun Point.neighbours() = listOf(
        this + Direction.East,
        this + Direction.South,
        this + Direction.West,
        this + Direction.North,
    )

    data class Cheat(
        val start: Point,
        val end: Point,
        val saving: Int
    )

    class MemoryRace(input: List<String>, val cheatTime: Int = 2) {
        val grid = CharGrid(input)
        val start = grid.allPoints.first { grid[it] == 'S' }
        val end = grid.allPoints.first { grid[it] == 'E' }

        val canonicalPath: List<Point> by lazy {

            val path = mutableListOf(start)
            var current = start
            while (current != end) {
                val next = current.neighbours()
                    .filter { grid[it] != '#' }
                    .filter { it !in path }
                    .also { if (it.size != 1) error("No distinct neighbour!") }
                    .first()
                path += next
                current = next
            }

            return@lazy path
        }

        fun allCheats(): Collection<Cheat> {
            return canonicalPath
                .flatMap { s ->
                    canonicalPath
                        .filter { e -> s.manhattanDistTo(e) <= cheatTime }
                        .map { e ->
                            val sw = canonicalPath.indexOf(s)
                            val ew = canonicalPath.indexOf(e)
                            Cheat(s, e, ew - sw - s.manhattanDistTo(e))
                        }.filter { it.saving > 0 }
                }
        }

        fun printGrid() = grid.printGrid()

        fun CharGrid.printGrid() {
            for (y in 0..<gridSize.y) {
                for (x in 0..<gridSize.x) {
                    print(get(x to y))
                }
                kotlin.io.println()
            }
        }

        fun isValidCheatPath(cheatPath: List<Point>): Cheat? {

            //println("Test cheat path $cheatPath")
            if (cheatPath.size < 2) return null
            if (cheatPath.size > cheatTime + 1) return null
            if (cheatPath.first() !in canonicalPath) return null
            if (cheatPath.last() !in canonicalPath) return null
            //if (!cheatPath.subList(1, cheatPath.lastIndex).all { grid[it] == '#' }) return null

            val startWeight = canonicalPath.indexOf(cheatPath.first())
            val endWeight = canonicalPath.indexOf(cheatPath.last())
            if (endWeight - startWeight - 2 < 1) return null
            return Cheat(cheatPath.first(), cheatPath.last(), endWeight - startWeight - 2)
        }

        fun isValidCheatBackup(cheatStart: Point, direction: Direction): Cheat? {

            //println("Check bakup $cheatStart dir $direction")
            if (cheatStart !in canonicalPath) return null
            if (grid[cheatStart + direction] != '#') return null
            val end = cheatStart + direction + direction
            if (end !in canonicalPath) return null

            val startWeight = canonicalPath.indexOf(cheatStart)
            val endWeight = canonicalPath.indexOf(end)
            if (startWeight >= endWeight) return null
            return Cheat(cheatStart, end, endWeight - startWeight - 2)
        }

        fun checkCheat(start: Point, direction: Direction, saving: Int) {
            check(isValidCheatBackup(start, direction).also { it.println() } == Cheat(start, start + direction + direction, saving))
            check(isValidCheatPath(listOf(start, start + direction, start + direction + direction)).also { it.println() } == Cheat(start, start + direction + direction, saving))
        }
    }

    fun testCheats(input: List<String>) {
        val memRace = MemoryRace(input)
        memRace.printGrid()
        println("Start: ${memRace.start}")
        println("Ende: ${memRace.end}")
        println("Path: ${memRace.canonicalPath}")
        println("Path Length: ${memRace.canonicalPath.size}")

        memRace.checkCheat(memRace.start, Direction.East, 4)
        memRace.checkCheat(Point(7, 1), Direction.East, 12)
        memRace.checkCheat(Point(9, 7), Direction.East, 20)
        memRace.checkCheat(Point(9, 7), Direction.East, 20)
        memRace.checkCheat(Point(8, 7), Direction.South, 38)
        memRace.checkCheat(Point(8, 7), Direction.South, 38)
        memRace.checkCheat(Point(7, 7), Direction.West, 64)
    }

    fun part12(input: List<String>, cheatTime: Int = 2, threshold: Int): Long {
        val memRace = MemoryRace(input, cheatTime)
        memRace.printGrid()
        println("Start: ${memRace.start}")
        println("Ende: ${memRace.end}")
        println("Path: ${memRace.canonicalPath}")
        println("Path Length: ${memRace.canonicalPath.size}")

        val allCheats = memRace.allCheats()
        val cheatsBySaving = allCheats.groupBy { it.saving }
        cheatsBySaving.keys.sorted().forEach { k -> println("${cheatsBySaving[k]?.size} x $k ps") }
        return allCheats.count { it.saving >= threshold }.toLong()
    }

    fun part1(input: List<String>) = part12(input, 2, 100)
    fun part2(input: List<String>) = part12(input, 20, 100)

    val testInput = readInput("Day20_test")
    check(testInput.size == 15)
    //testCheats(testInput)
    check(part1(testInput).also { it.println() } == 0L)
    check(part2(testInput).also { it.println() } == 0L)

    val input = readInput("Day20")
    check(input.size == 141)
    //part1(input).println()
    check(part1(input).also { it.println() } == 1411L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 1010263L)
}
