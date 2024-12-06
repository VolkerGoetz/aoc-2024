class Grid(val input: List<String>) {
    data class Point(val x: Int, val y: Int)
    data class Direction(val dx: Int, val dy: Int, val mark: Char)

    infix fun Int.to(that: Int): Point = Point(this, that)
    infix fun Point.moveBy(direction: Direction) =
        Point(this.x + direction.dx, this.y + direction.dy)

    val obstacle = '#'
    val up = Direction(0, -1, 'u')
    val down = Direction(0, 1, 'd')
    val left = Direction(-1, 0, 'l')
    val right = Direction(1, 0, 'r')

    val size: Point = input[0].length to (input.size)
    var grid: ArrayList<ArrayList<Char>> = ArrayList()
    var currentDirection = up
    lateinit var currentPosition: Point

    init {
        for (x in 0..size.x - 1) {
            var col = ArrayList<Char>()
            for (y in 0..size.y - 1) {
                //println("$x | $y")
                col.add(input[y][x])
            }
            grid.add(col)
        }

        for (x in grid.indices) {
            for (y in grid[x].indices) {
                if (grid[x][y] == '^') {
                    currentPosition = x to y
                    break
                }
            }
        }

        if (!this::currentPosition.isInitialized)
            throw IllegalStateException("No starting point (^) in grid found")

        setValue(currentPosition, up.mark)
    }

    fun copy() =
        Grid(this.input)

    fun copyWithAdditionalObstacle(obstaclePos: Point): Grid =
        copy().also { g2 -> g2.setValue(obstaclePos, obstacle) }

    fun printGrid(): Unit {

        for (y in 0..size.y - 1) {
            for (x in 0..size.x - 1) {
                print(grid[x][y])
            }
            "".println()
        }
    }

    fun turn() {
        when (currentDirection) {
            up -> currentDirection = right
            right -> currentDirection = down
            down -> currentDirection = left
            left -> currentDirection = up
        }
    }

    fun outOfGrid(position: Point) =
        when {
            position.x < 0 -> true
            position.x >= size.x -> true
            position.y < 0 -> true
            position.y >= size.y -> true
            else -> false
        }

    fun isObastacle(position: Point) =
        grid[position.x][position.y] == obstacle

    fun setValue(position: Point, value: Char) {
        grid[position.x][position.y] = value
    }

    fun onLoop(nextPos: Point) =
        grid[nextPos.x][nextPos.y] == currentDirection.mark

    fun walkPatrol(): Boolean {

        while (true) {
            val nextPos = currentPosition moveBy currentDirection

            when {
                outOfGrid(nextPos) -> break
                isObastacle(nextPos) -> turn()
                onLoop(nextPos) -> return true
                else -> {
                    currentPosition = nextPos
                    setValue(currentPosition, currentDirection.mark)
                }
            }
        }

        return false
    }

    fun tryLoops(): Long {

        var numLoops = 0
        for (x in 0..size.x - 1) {
            for (y in 0..size.y - 1) {
                if (grid[x][y] == obstacle) {
                    continue
                }

                val g2 = copyWithAdditionalObstacle(Point(x, y))
                if (g2.walkPatrol()) {
                    ++numLoops
                }
            }
        }

        return numLoops.toLong()
    }

    fun tryLoopsParallel(): Long {

        var numLoops =
            (0..size.x - 1).asIterable().flatMap { x ->
                (0..size.y - 1).asIterable()
                    .map { y -> Point(x, y) }
            }
                .parallelStream()
                .filter { grid[it.x][it.y] != obstacle }
                .filter { it -> copyWithAdditionalObstacle(it).walkPatrol() }
                .count()

        return numLoops.toLong()
    }

    fun Char.isVisited() =
        when (this) {
            up.mark, down.mark, left.mark, right.mark -> true
            else -> false
        }

    fun countVisitedPlaces() =
        grid.sumOf { it.count { it.isVisited() } }
}

fun main() {

    fun part1(input: List<String>): Long {

        val grid = Grid(input)
        //grid.printGrid()
        grid.walkPatrol()
        val cnt = grid.countVisitedPlaces()
        return cnt.toLong()
    }

    fun part2(input: List<String>): Long {

        //return Grid(input).tryLoops()
        return Grid(input).tryLoopsParallel()
    }

    val testInput = readInput("Day06_test")
    check(testInput.size == 10)
    check(part1(testInput).also { it.println() } == 41L)
    check(part2(testInput).also { it.println() } == 6L)

    val input = readInput("Day06")
    check(input.size == 130)
    part1(input).println()
    part2(input).println()
}
