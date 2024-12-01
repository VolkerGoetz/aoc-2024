import kotlin.math.abs

fun main() {
    fun part1(input: List<String>) = input.leftList().sorted()
        .zip(input.rightList().sorted())
        .sumOf { abs(it.first - it.second) }

    fun part2(input: List<String>): Int {
        val rightList = input.rightList()
        return input.leftList().sumOf { i -> i * rightList.count { it == i } }
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("3   4")) == 1)
    check(part2(listOf("3   4")) == 0)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
