class Laboratory(val input: List<String>) {
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
    var grid = Grid(input)
    var currentDirection = up
    var currentPosition = grid.allPoints.first { grid[it] == '^' }

    init {
        setValue(currentPosition, up.mark)
    }

    fun copy() =
        Laboratory(this.input)

    fun copyWithAdditionalObstacle(obstaclePos: Point): Laboratory =
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
        for (p in grid.allPoints) {
            if (grid[p] == obstacle) {
                continue
            }

            val g2 = copyWithAdditionalObstacle(p)
            if (g2.walkPatrol()) {
                ++numLoops
            }
        }


        return numLoops.toLong()
    }

    fun tryLoopsParallel(): Long {

        var numLoops =
            grid.allPoints
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

        val grid = Laboratory(input)
        //grid.printGrid()
        grid.walkPatrol()
        val cnt = grid.countVisitedPlaces()
        return cnt.toLong()
    }

    fun part2(input: List<String>): Long {

        //return Grid(input).tryLoops()
        return Laboratory(input).tryLoopsParallel()
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
