fun main() {

    val moduloPrune = 16_777_216L // = 2^24 = 1000000000000000000000000
    fun monkeyRandom(oldSecret: Long): Long {

        val x1 = oldSecret * 64L // shl 6 = * 1000000
        val x2 = x1 xor oldSecret
        val x3 = x2 % moduloPrune // 2^24

        val x4 = x3 / 32L // shr 5
        val x5 = x3 xor x4
        val x6 = x5 % moduloPrune

        val x7 = x6 * 2048L // shl 11
        val x8 = x7 xor x6
        val x9 = x8 % moduloPrune

        return x9
    }

    fun monkeyRandomIterations(start: Long, rounds: Int): Long {
        var s = start
        repeat(2000) {
            s = monkeyRandom(s)
        }
        return s
    }

    data class Iteration(val secret: Long, val digit: Long, val difference: Long)

    fun monkeyRandomData(start: Long, rounds: Int): List<Iteration> =
        buildList {
            var prev = Iteration(start, start % 10L, 0)
            repeat(rounds) {
                val s = monkeyRandom(prev.secret)
                val curr = Iteration(s, s % 10L, s % 10L - prev.digit)
                add(curr)
                prev = curr
            }
        }


    fun part1(input: List<String>): Long {
        return input
            .map { it.toLong() }
            .sumOf { monkeyRandomIterations(it, 2000) }

    }

    fun part2(input: List<String>): Long {
        val allData = input.map { monkeyRandomData(it.toLong(), 2000) }

        val allGrouped = buildList {
            for (secretData in allData) {
                val map = mutableMapOf<List<Long>, Long>()
                secretData.windowed(4) { sublist ->
                    val dists = sublist.map { it.difference }
                    val price = sublist.last().digit
                    map.putIfAbsent(dists, price)
                }
                add(map)
            }
        }

        //println(allGrouped.size)

        val allSeq = allData.flatMap { secretData -> secretData.windowed(4) { it.map { it.difference } } }.distinct()
        //allSeq.size.println()

        val maxTotalPrice = allSeq.maxOfOrNull { seq ->
            allGrouped.sumOf { it.getOrElse(seq) { 0L } }
        }

        return maxTotalPrice!!
    }

    val testInput1 = readInput("Day22_test1")
    check(testInput1.size == 4)
    check(part1(testInput1).also { it.println() } == 37327623L)

    val testInput2 = readInput("Day22_test2")
    check(testInput2.size == 4)
    check(part2(testInput2).also { it.println() } == 23L)

    val input = readInput("Day22")
    check(input.size == 1842)
    //part1(input).println()
    check(part1(input).also { it.println() } == 15613157363L)
    //part2(input).println()
    check(part2(input).also { it.println() } == 1784L)
}
