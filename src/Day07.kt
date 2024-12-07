import java.util.*

class Equation {

    val result: Long
    val numbers: List<Long>
    var validOps = ""

    constructor(line: String) {

        val parts = line.split("""\s*:\s*""".toRegex())
        result = parts[0].toLong()
        numbers = parts[1].split("""\s+""".toRegex()).map { it.toLong() }
    }

    override fun toString(): String {
        return "Equation(result=$result, numbers=$numbers ops='$validOps')"
    }

    fun solveable(): Boolean {

        val numberQueue: Queue<Long> = LinkedList(numbers)
        val first = numberQueue.poll()
        val second = numberQueue.poll()
        for (op in Op.entries) {
            val x = op.f(first, second)
            if (solve(x, LinkedList(numberQueue), "$first $op $second")) {
                return true
            }
        }
        return false
    }

    private fun solve(inval: Long, numberQueue: Queue<Long>, ops: String): Boolean {
        if (numberQueue.isEmpty()) {
            //println("$ops = $inval -> ${inval == result}")
            if (inval == result) {
                validOps = ops
                return true
            }
            return false
        }

        if (inval > result) {
            return false
        }

        val next = numberQueue.poll()
        for (op in Op.entries) {
            val x2 = op.f(inval, next)
            if (solve(x2, LinkedList(numberQueue), "$ops $op $next")) {
                return true
            }
        }
        return false
    }

    fun solveableWithConcat(): Boolean {

        val numberQueue: Queue<Long> = LinkedList(numbers)
        val first = numberQueue.poll()
        val second = numberQueue.poll()

        for (op in Op2.entries) {
            val x = op.f(first, second)
            if (solveWithConcat(x, LinkedList(numberQueue), "$first $op $second")) {
                return true
            }
        }
        return false
    }

    private fun solveWithConcat(inval: Long, numberQueue: Queue<Long>, ops: String): Boolean {
        if (numberQueue.isEmpty()) {
            //println("$ops = $inval -> ${inval == result}")
            if (inval == result) {
                validOps = ops
                return true
            }
            return false
        }

        if (inval > result) {
            return false
        }

        val next = numberQueue.poll()
        for (op in Op2.entries) {
            val x2 = op.f(inval, next)
            if (solveWithConcat(x2, LinkedList(numberQueue), "$ops $op $next")) {
                return true
            }
        }
        return false
    }
}

enum class Op(val mnemonic: Char, val f: (l: Long, r: Long) -> Long) {

    PLUS('+', Long::plus),
    TIMES('*', Long::times), ;

    override fun toString(): String {
        return mnemonic.toString()
    }
}

enum class Op2(val mnemonic: String, val f: (l: Long, r: Long) -> Long) {

    PLUS("+", Long::plus),
    TIMES("*", Long::times),
    CONCAT("||", { l, r -> (l.toString() + r.toString()).toLong() });

    override fun toString(): String {
        return mnemonic.toString()
    }
}

fun List<String>.parseEquations() =
    map { string -> Equation(string) }


fun main() {

    fun part1(input: List<String>): Long {

        val equations = input.parseEquations()
        val res = equations
            .filter { e -> e.solveable() }
            //.also { it.forEach { e -> e.println() } }
            .sumOf { it.result }

        return res.toLong()
    }

    fun part2(input: List<String>): Long {

        val equations = input.parseEquations()
        val res = equations
            .filter { e -> e.solveableWithConcat() }
            //.also { it.forEach { e -> e.println() } }
            .sumOf { it.result }

        return res.toLong()
    }

    check(part1(listOf("292: 11 6 16 20")).also { it.println() } == 292L)
    check(part1(listOf("4859: 35 78 43")).also { it.println() } == 4859L)

    check(part2(listOf("156: 15 6")).also { it.println() } == 156L)
    check(part2(listOf("7290: 6 8 6 15")).also { it.println() } == 7290L)
    check(part2(listOf("192: 17 8 14")).also { it.println() } == 192L)

    val testInput = readInput("Day07_test")
    check(testInput.size == 9)
    check(part1(testInput).also { it.println() } == 3749L)
    check(part2(testInput).also { it.println() } == 11387L)

    val input = readInput("Day07")
    check(input.size == 850)
    part1(input).println()
    part2(input).println()
}
