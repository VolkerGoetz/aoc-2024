import kotlin.math.abs
import kotlin.math.roundToLong

data class ClawMachine(
    val ax: Long, val ay: Long,
    val bx: Long, val by: Long,
    val px: Long, val py: Long,
) {
    val dax: Double
    val day: Double
    val dbx: Double
    val dby: Double
    val dpx: Double
    val dpy: Double

    init {
        dax = ax.toDouble()
        day = ay.toDouble()
        dbx = bx.toDouble()
        dby = by.toDouble()
        dpx = px.toDouble()
        dpy = py.toDouble()
    }

    fun solve(): Pair<Long, Long>? {
        // m = (PY/AY - PX/AX) /  (BY/AY - BX/AX)
        val byayMbxax = dby / day - dbx / dax
        if (abs(byayMbxax) == 0.0) return null
        val m = (dpy / day - dpx / dax) / byayMbxax
        // n = (PX - m*BX)/AX
        val n = (dpx - m * dbx) / dax

        val ml = m.roundToLong()
        val nl = n.roundToLong()

        // sort out non-integer (real) solutions
        if (abs(nl.toDouble() - n) > 0.01) return null
        if (abs(ml.toDouble() - m) > 0.01) return null

        return Pair(nl, ml)
    }
}


fun main() {
    fun test1() {

        val res = ClawMachine(
            94, 34,
            22, 67,
            8400, 5400,
        ).solve()
        res.println()
        check(res != null)
        check(res.first == 80L && res.second == 40L)
    }

    fun test2() {

        val res = ClawMachine(
            26, 66,
            67, 21,
            12748, 12176
        ).solve()
        res.println()
        check(res == null)
    }

    fun test3() {

        val res = ClawMachine(
            17, 86,
            84, 37,
            7870, 6450
        ).solve()
        res.println()
        check(res != null)
        check(res.first == 38L && res.second == 86L)
    }

    fun test4() {

        val res = ClawMachine(
            69, 23,
            27, 71,
            18641, 10279
        ).solve()
        res.println()
        check(res == null)
    }

    fun String.parseLine(): Pair<Long, Long> =
        """.+?(\d+).+?(\d+)""".toRegex().find(this)?.let {
            Pair(it.groupValues[1].toLong(), it.groupValues[2].toLong())
        } ?: throw IllegalArgumentException()


    fun parseInput(input: List<String>, addToPrice: Long = 0L) =
        input.chunked(4).map { mlines ->
            val (ax, ay) = mlines[0].parseLine()
            val (bx, by) = mlines[1].parseLine()
            val (px, py) = mlines[2].parseLine()
            ClawMachine(ax, ay, bx, by, px + addToPrice, py + addToPrice)
        }

    //test1()
    //test2()
    //test3()
    //test4()

    fun part1(input: List<String>) =
        parseInput(input)
            .mapNotNull { it.solve() }
            .sumOf { it.first * 3 + it.second }

    fun part2(input: List<String>) =
        parseInput(input, 10000000000000)
            .mapNotNull { it.solve() }
            .sumOf { it.first * 3 + it.second }


    val testInput1 = readInput("Day13_test")
    check(testInput1.size == 15)
    check(part1(testInput1).also { it.println() } == 480L)
    check(part2(testInput1).also { it.println() } > -1L)

    val input = readInput("Day13")
    check(input.size == 1279)
    //part1(input).println()
    check(part1(input).also { it.println() } == 29023L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 96787395375634L)
}

/*

Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400


x = n | y = m

n = Button A
m = Button B

x1 = n
x2 = m
y1 = px
y2 = py

ax * x1 + bx * x2 = y1
n * AX + m * BX = PX
n * AX = (PX - m  * BX)
n = (PX - m  * BX) / AX
n = -m*BX/AX + PX/AX

ay * x1 + by * x2 = y2
n * AY + m * BY = PY
n * AY = (PY - m * BY)
n = (PY - m * BY) / AY
n = -m*BY/AY + PY/AY


-m*BX/AX + PX/AX = -m*BY/AY + PY/AY

m*BY/AY - m*BX/AX  = PY/AY - PX/AX
m * (BY/AY - BX/AX) = PY/AY - PX/AX
m = (PY/AY - PX/AX) /  (BY/AY - BX/AX)

m = (5400/34 - 8400/94) / (67/34 - 22/94)
m = 40



n * AX + m * BX = PX
n*AX = PX - m*BX
n = (PX - m*BX)/AX
n = (8400 - 40*22)/94
n = 80

*/
