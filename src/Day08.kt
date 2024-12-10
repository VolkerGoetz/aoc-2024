data class Antenna(val pos: Point, val freq: Char)

fun antennasByFrequency(grid: CharGrid) =
    grid.allPoints
        .map { Antenna(it, grid[it]) }
        .filterNot { it.freq == '.' }
        .filterNot { it.freq == '#' }
        .groupBy(Antenna::freq, Antenna::pos)

fun getAntennaPairs(antennasByFreq: Map<Char, List<Point>>) =
    antennasByFreq.values.flatMap { freqPoints ->
        freqPoints.flatMap { a ->
            freqPoints.map { b -> a to b }
        }
    }.filterNot { it.first == it.second }


fun main() {

    fun part1(input: List<String>): Long {

        val antennaGrid = CharGrid(input)
        val antennasByFreq = antennasByFrequency(antennaGrid)

        antennasByFreq.forEach { f, al -> println("$f -> $al") }

        val antinodes = getAntennaPairs(antennasByFreq)
            .map { pair -> pair.second + pair.vector() }
            .filter { p -> p in antennaGrid }
            .toSet()

        //antinodes.forEach { p -> println("$p") }

        return antinodes.size.toLong()
    }

    fun part2(input: List<String>): Long {

        val antennaGrid = CharGrid(input)
        val antennasByFreq = antennasByFrequency(antennaGrid)

        val a = 'a' in antennasByFreq

        val antinodes = getAntennaPairs(antennasByFreq)
            .flatMap { pair ->
                buildList {
                    add(pair.first)
                    add(pair.second)
                    var node = pair.second + pair.vector()
                    while (node in antennaGrid) {
                        add(node)
                        node = node + pair.vector()
                    }
                }
            }
            .filter { p -> p in antennaGrid }
            .toSet()

        //antinodes.forEach { p -> println("$p") }

        return antinodes.size.toLong()
    }

    val testInput1 = readInput("Day08_test_1")
    check(testInput1.size == 10)
    check(part1(testInput1).also { it.println() } == 2L)

    val testInput2 = readInput("Day08_test_2")
    check(testInput2.size == 10)
    check(part2(testInput2).also { it.println() } == 9L)

    val testInput = readInput("Day08_test")
    check(testInput.size == 12)
    check(part1(testInput).also { it.println() } == 14L)
    check(part2(testInput).also { it.println() } == 34L)

    val input = readInput("Day08")
    check(input.size == 50)
    part1(input).println()
    part2(input).println()
}
