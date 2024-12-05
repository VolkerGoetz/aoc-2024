fun main() {

    data class Rule(val first: Int, val second: Int) {

        fun validForPositions(positions: Map<Int, Int>): Boolean {
            val pos1 = positions[first]
            val pos2 = positions[second]
            return when {
                pos1 == null || pos2 == null -> true
                else -> pos1 < pos2
            }
        }

        fun swap(first: Int, second: Int) =
            when {
                this.first == second && this.second == first -> true
                else -> false
            }
    }

    fun readRules(input: List<String>) =
        input.takeWhile { it.isNotBlank() }
            .map { line ->
                val args = line.split("|")
                Rule(args[0].toInt(), args[1].toInt())
            }

    fun readSequences(input: List<String>) =
        input.takeLastWhile { it.isNotBlank() }
            .map { line ->
                line.split(",").map { it.toInt() }
            }

    fun pagePositions(sequence: List<Int>): Map<Int, Int> =
        buildMap {
            sequence.forEachIndexed { index, value -> put(value, index) }
        }

    fun List<Int>.isSequenceValid(rules: List<Rule>): Boolean {
        val positions = pagePositions(this)
        return rules.all { it.validForPositions(positions) }
    }

    fun List<Int>.middleValue() = this[size / 2]

    fun List<List<Int>>.middleSum() = sumOf { it.middleValue() }

    fun part1(input: List<String>): Long {
        val rules = readRules(input)
        val sequences = readSequences(input)

        val validSequences = sequences
            .filter { s -> s.isSequenceValid(rules) }

        val result = validSequences.middleSum()
        return result.toLong()
    }

    fun MutableList<Int>.swap(i1: Int, i2: Int) {
        val tmp = this[i1]
        this[i1] = this[i2]
        this[i2] = tmp
    }

    fun List<Int>.reorder(rules: List<Rule>): List<Int> {
        var result = this.toMutableList()
        for (i1 in 0..this.lastIndex) {
            for (i2 in i1 + 1..this.lastIndex) {
                for (rule in rules) {
                    if (rule.swap(result[i1], result[i2])) {
                        result.swap(i1, i2)
                        break
                    }
                }
            }
        }

        return result
    }

    fun part2(input: List<String>): Long {

        // 75,97,47,61,53 becomes 97,75,47,61,53.
        val rules = readRules(input)
        val sequences = readSequences(input)

        val invalidSequences = sequences
            .filter { s -> !s.isSequenceValid(rules) }

        val revalidSequenes = invalidSequences
            .map { s -> s.reorder(rules) }
            .filter { s -> s.isSequenceValid(rules) }

        val result = revalidSequenes.middleSum()
        return return result.toLong()
    }

    val testInput = readInput("Day05_test")
    check(testInput.size == 28)
    check(part1(testInput).also { it.println() } == 143L)
    check(part2(testInput).also { it.println() } == 123L)

    val input = readInput("Day05")
    check(input.size == 1362)
    part1(input).println()
    check(part1(input) == 5091L)
    part2(input).println()
}
