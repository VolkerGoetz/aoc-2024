fun main() {


    fun part1(input: List<String>): Long {

        val mulRegex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

        return input
            .flatMap { line ->
                mulRegex.findAll(line)
                    .map { it.groupValues[1].toLong() * it.groupValues[2].toLong() }
            }.sum()
    }

    fun part2(input: List<String>): Long {

        val mulRegex = """(mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\))""".toRegex()
        var enabled = true
        return input.flatMap { line ->
            mulRegex.findAll(line).map {
                val verb = it.groupValues[1]
                when {
                    verb.startsWith("mul") && enabled -> it.groupValues[2].toLong() * it.groupValues[3].toLong()
                    verb == "do()" -> {
                        enabled = true
                        0L
                    }

                    verb == "don't()" -> {
                        enabled = false
                        0L
                    }

                    else -> 0L
                }
            }
        }.sum()
    }


    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("mul(2,4)")) == 8L)
    check(part1(listOf("mul(2, 4)")) == 0L)
    check(part1(listOf("xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))")) == 161L)

    check(part2(listOf("mul(2,4)")) == 8L)
    check(part2(listOf("mul(2, 4)")) == 0L)
    check(part2(listOf("don't()mul(2,4)")) == 0L)
    check(part2(listOf("don't()mul(2,4)do()mul(3,4)")) == 12L)
    check(part2(listOf("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")) == 48L)

    val input = readInput("Day03")
    check(input.size == 6)
    part1(input).println()
    part2(input).println()
}
