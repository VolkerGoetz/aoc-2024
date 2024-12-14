fun main() {
    data class LPoint(val x: Long, val y: Long) : Comparable<LPoint> {
        override fun compareTo(other: LPoint) =
            when {
                this.y == other.y -> this.x.compareTo(other.x)
                else -> this.y.compareTo(other.y)
            }
    }

    operator fun LPoint.plus(that: LPoint) = LPoint(this.x + that.x, this.y + that.y)

    operator fun LPoint.times(that: Int) = LPoint(this.x * that, this.y * that)

    data class Robot(val pos: LPoint, val velocity: LPoint) {

        fun move(times: Int) = pos + (velocity * times)
        fun moveWrapped(times: Int, size: LPoint) =
            move(times).let {
                LPoint(it.x.mod(size.x), it.y.mod(size.y))
            }
    }

    fun Robot(line: String) =
        """p=(\d+),(\d+)\s+v=(-?\d+),(-?\d+)""".toRegex()
            .find(line)?.let {
                Robot(
                    LPoint(it.groupValues[1].toLong(), it.groupValues[2].toLong()),
                    LPoint(it.groupValues[3].toLong(), it.groupValues[4].toLong())
                )
            } ?: throw IllegalArgumentException()


    fun test1() {
        //val r = Robot("p=0,4 v=3,-3")
        val r = Robot("p=2,4 v=2,-3")
        r.println()

        val size = LPoint(11, 7)
        val p1 = r.moveWrapped(1, size).also { it.println() }
        check(p1.x == 4L && p1.y == 1L)

        val p2 = r.moveWrapped(2, size).also { it.println() }
        check(p2.x == 6L && p2.y == 5L)

        val p3 = r.moveWrapped(3, size).also { it.println() }
        check(p3.x == 8L && p3.y == 2L)
    }

    fun printRobotPositions(robots: List<LPoint>, size: LPoint) {
        for (y in 0..size.y - 1) {
            for (x in 0..size.x - 1) {
                print(if (LPoint(x, y) in robots) 'X' else '.')
            }
            println()
        }
    }

    fun part1(input: List<String>, size: LPoint = LPoint(101L, 103L)): Long {

        val times = 100

        return input
            .map { l -> Robot(l) }
            .map { r -> r.moveWrapped(times, size) }
            .sorted()
            .filter { p -> p.x != size.x / 2L && p.y != size.y / 2L }
            .partition { p -> p.x < size.x / 2 }
            .let { (left, right) ->
                left.partition { p -> p.y < size.y / 2L }
                    .let { (top, bottom) ->
                        top.size.toLong() * bottom.size.toLong()
                    } * right.partition { p -> p.y < size.y / 2L }
                    .let { (top, bottom) ->
                        top.size.toLong() * bottom.size.toLong()
                    }
            }
    }

    fun LPoint.hasAnyNeighboursIn(robots: List<LPoint>): Boolean {

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx != 0 || dy != 0) {
                    if (robots.contains(LPoint(this.x + dx, this.y + dy))) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun part2(input: List<String>, size: LPoint = LPoint(101L, 103L)): Long {

        val robots = input.map { Robot(it) }
        var iter = 0
        var rx: List<LPoint>
        var found = false
        do {
            ++iter
            rx = robots.map { r -> r.moveWrapped(iter, size) }
            //found = rx.count { p -> p.hasAnyNeighboursIn(rx) } > robots.size / 1.5
            // faster but may not ALWAYS be true
            found = rx.distinct().size == robots.size
        } while (!found && iter < size.x * size.y)

        if (found) printRobotPositions(rx, size)
        return if (found) iter.toLong() else -1L
    }

    //test1()

    val testInput1 = readInput("Day14_test")
    check(testInput1.size == 12)
    check(part1(testInput1, LPoint(11, 7)).also { it.println() } == 12L)

    val input = readInput("Day14")
    check(input.size == 500)
    ////part1(input).println()
    check(part1(input).also { it.println() } == 215987200L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 8050L)
}
