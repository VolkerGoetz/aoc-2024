data class Plot(
    val type: Char,
    var typeN: Char? = null,
    var typeE: Char? = null,
    var typeS: Char? = null,
    var typeW: Char? = null,
)

typealias PlotRegion = List<Pair<Point, Plot>>

class PlotGrid(lines: List<String>) : Grid<Plot>(lines, { c: Char -> Plot(c) }) {

    val pointsAddedToArea = mutableListOf<Point>()

    init {
        for (p in allPoints) {
            this[p].typeN = getOrNull(p + Direction.North)?.type
            this[p].typeE = getOrNull(p + Direction.East)?.type
            this[p].typeS = getOrNull(p + Direction.South)?.type
            this[p].typeW = getOrNull(p + Direction.West)?.type
        }
    }

    private fun getPartialTree(start: Point, type: Char): PlotRegion {

        if (start !in this) return emptyList()
        if (start in pointsAddedToArea) return emptyList()
        if (get(start).type != type) return emptyList()

        return buildList {
            add(start to get(start))
            pointsAddedToArea += start
            addAll(getPartialTree(start + Direction.North, type))
            addAll(getPartialTree(start + Direction.East, type))
            addAll(getPartialTree(start + Direction.South, type))
            addAll(getPartialTree(start + Direction.West, type))
        }
    }

    fun getSubTrees(): List<Pair<Char, PlotRegion>> =
        allPoints.map { p -> this[p].type to getPartialTree(p, get(p).type) }
            .filter { it.second.isNotEmpty() }
}

fun Pair<Char, PlotRegion>.area() = this.second.size

fun Pair<Char, PlotRegion>.perimeterPoints(): Pair<List<Point>, List<Point>> {

    val type = this.first
    return buildList {
        for (p in this@perimeterPoints.second) {
            if (p.second.typeN != type) add(p.first * 2)
            if (p.second.typeS != type) add(p.first * 2 + Direction.South)
        }
    } to buildList {
        for (p in this@perimeterPoints.second) {
            if (p.second.typeE != type) add(p.first * 2)
            if (p.second.typeW != type) add(p.first * 2 + Direction.West)
        }
    }
}

fun Pair<Char, PlotRegion>.perimeterLength() =
    this.perimeterPoints().let { it.first.size + it.second.size }

fun Pair<Char, PlotRegion>.perimeterSides(): Int {

    val points = this.perimeterPoints()

    val vertSides = points.second.groupBy { p -> p.x }
        .map { (_, l) ->
            l.distinct().sorted().zipWithNext().map { (first, next) ->
                when {
                    first.y + 2 == next.y -> 0
                    else -> 1
                }
            }.sum().inc()
        }.sum()


    val horizSides = points.first.groupBy { p -> p.y }
        .map { (_, l) ->
            l.distinct().sorted().zipWithNext().map { (first, next) ->
                when {
                    first.x + 2 == next.x -> 0
                    else -> 1
                }
            }.sum().inc()
        }.sum()

    return vertSides + horizSides
}

fun main() {

    fun part1(input: List<String>) = PlotGrid(input)
        .getSubTrees()
        .sumOf { it.area() * it.perimeterLength() }
        .toLong()

    fun part2(input: List<String>) = PlotGrid(input)
        .getSubTrees()
        .sumOf { (it.area() * it.perimeterSides()) }
        .toLong()

    val testInput1 = readInput("Day12_test_1") // ABCDE
    check(testInput1.size == 4)
    check(part1(testInput1).also { it.println() } == 140L)
    check(part2(testInput1).also { it.println() } == 80L)

    val testInput2 = readInput("Day12_test_2") // X and O
    check(testInput2.size == 5)
    check(part1(testInput2).also { it.println() } == 772L)
    check(part2(testInput2).also { it.println() } == 436L)

    val testInput3 = readInput("Day12_test_3") // E-Shape + X
    check(testInput3.size == 5)
    check(part2(testInput3).also { it.println() } == 236L)

    val testInput4 = readInput("Day12_test_4") // As and Bs
    check(testInput4.size == 6)
    check(part2(testInput4).also { it.println() } == 368L)

    val testInput = readInput("Day12_test")
    check(testInput.size == 10)
    check(part1(testInput).also { it.println() } == 1930L)
    check(part2(testInput).also { it.println() } == 1206L)


    val input = readInput("Day12")
    check(input.size == 140)
    //part1(input).println()
    check(part1(input).also { it.println() } == 1319878L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 784982L)
}
