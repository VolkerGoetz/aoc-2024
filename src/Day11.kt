fun main() {

    val cache = mutableMapOf<Pair<Long, Int>, Long>()
    fun blink(number: Long, count: Int): Long {

        cache[number to count]?.let { return it }

        val numStr = number.toString()
        return when {
            count == 0 -> 1L
            number == 0L -> blink(1L, count - 1)
            numStr.length % 2 == 0 ->
                blink(numStr.take(numStr.length / 2).toLong(), count - 1) + blink(numStr.takeLast(numStr.length / 2).toLong(), count - 1)

            else -> blink(number * 2024L, count - 1)
        }.also {
            cache[number to count] = it
        }
    }


    fun part1(input: List<String>, iterations: Int = 25): Long =
        input[0]
            .split("\\s+".toRegex())
            .map { s -> s.toLong() }
            .sumOf { n -> blink(n, iterations) }


    fun part2(input: List<String>, iterations: Int = 75) =
        part1(input, iterations)

    check(part1(listOf("0 1 10 99 999"), 1).also { it.println() } == 7L)
    check(part1(listOf("125 17"), 6).also { it.println() } == 22L)
    //check(part1(listOf("1"), 75).also { it.println() } == 22L)
    //check(part1(listOf("17639"), 75).also { it.println() } == 22L)

    val input = readInput("Day11")
    check(input.size == 1)
    //part1(input).println()
    check(part1(input).also { it.println() } == 203228L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 240884656550923L)
}
