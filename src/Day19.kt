fun main() {
    fun List<String>.towelPatterns() = first().split("""\s*,\s*""".toRegex()).sorted()
    fun List<String>.designs() = takeLastWhile { it.isNotEmpty() }

    fun part1WithRegex(input: List<String>): Long {
        val stripePatterns = input.towelPatterns()
            .joinToString("|", "(", ")+")
            .toRegex()

        return input.designs().count { stripePatterns.matches(it) }.toLong()
    }

    fun hasAnyMatch(towelPatterns: List<String>, design: String): Boolean {

        val cache = mutableMapOf("" to false)
        fun hasAnyMatchPriv(towelPatterns: List<String>, designTail: String): Boolean =
            cache.getOrPut(designTail) {
                towelPatterns
                    .filter { designTail.startsWith(it) }
                    .any { designTail == it || hasAnyMatchPriv(towelPatterns, designTail.removePrefix(it)) }
            }

        return hasAnyMatchPriv(towelPatterns, design)
    }

    fun allMatches(towelPatterns: List<String>, design: String): Long {

        val tailCache = mutableMapOf<String, Long>()
        fun allMatchesRecursive(towelPatterns: List<String>, designTail: String): Long =
            tailCache.getOrPut(designTail) {
                towelPatterns
                    .filter { towel -> designTail.startsWith(towel) }
                    .sumOf { towel ->
                        when {
                            designTail == towel -> 1L
                            else -> allMatchesRecursive(towelPatterns, designTail.removePrefix(towel))
                        }
                    }
            }
        return allMatchesRecursive(towelPatterns, design)
    }

    fun part1(input: List<String>): Long {
        val towelPatterns = input.towelPatterns()
        val testPatterns = input.designs()
        val res = testPatterns.map { des -> hasAnyMatch(towelPatterns, des) }.count { it }
        return res.toLong()
    }

    fun part2(input: List<String>): Long {
        val towelPatterns = input.towelPatterns()
        val testPatterns = input.designs()
        val res = testPatterns.sumOf { des -> allMatches(towelPatterns, des).toLong() }
        return res
    }

    val testInput = readInput("Day19_test")
    check(testInput.size == 10)
    check(part1(testInput).also { it.println() } == 6L)
    check(part2(testInput).also { it.println() } == 16L)

    val input = readInput("Day19")
    check(input.size == 402)
    //part1(input).println()
    check(part1(input).also { it.println() } == 228L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 584553405070389L)
}
