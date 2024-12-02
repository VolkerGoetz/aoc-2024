import kotlin.math.abs

fun main() {
    fun String.toLongList() = this
        .split("""\s+""".toRegex())
        .map { it.toLong() }

    fun isReportSafe(report: List<Long>): Int {
        var lastDistance = 0L
        for (index in 0..report.lastIndex - 1) {
            val x1 = report[index]
            val x2 = report[index + 1]
            val d = x2 - x1

            when {
                lastDistance < 0 && d > 0 -> return 0
                lastDistance > 0 && d < 0 -> return 0
                d == 0L -> return 0
                abs(d) > 3 -> return 0
                else -> lastDistance = d
            }
        }
        return 1
    }

    fun isReportSafeWithDampener(report: List<Long>): Int {
        if (isReportSafe(report) == 1) {
            return 1
        }

        for (index in 0..report.lastIndex) {
            val dampedReport = report.toMutableList()
            dampedReport.removeAt(index)
            if (isReportSafe(dampedReport) == 1)
                return 1
        }

        return 0
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.toLongList() }
            .sumOf { isReportSafe(it) }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toLongList() }
            .sumOf { isReportSafeWithDampener(it) }
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("7 6 4 2 1")) == 1)
    check(part1(listOf("1 2 7 8 9")) == 0)
    check(part1(listOf("9 7 6 2 1")) == 0)
    check(part1(listOf("1 3 2 4 5")) == 0)
    check(part1(listOf("8 6 4 4 1")) == 0)
    check(part1(listOf("1 3 6 7 9")) == 1)


    check(part2(listOf("7 6 4 2 1")) == 1)
    check(part2(listOf("1 2 7 8 9")) == 0)
    check(part2(listOf("9 7 6 2 1")) == 0)
    check(part2(listOf("1 3 2 4 5")) == 1)
    check(part2(listOf("8 6 4 4 1")) == 1)
    check(part2(listOf("1 3 6 7 9")) == 1)


    val testInput = readInput("Day02_test")
    check(testInput.size == 6)
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day02")
    check(input.size == 1000)
    part1(input).println()
    part2(input).println()
}
