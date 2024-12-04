fun main() {

    val xmas = "XMAS"

    fun part1(input: List<String>): Long {

        val numLines = input.size
        val numCols = input.first().length

        var allForwardLines = mutableListOf<String>()

        // all lines
        allForwardLines.addAll(input)

        // all rows
        for (c in 0..numCols - 1) {
            var row = ""
            for (line in input) {
                row += line[c]
            }
            allForwardLines.add(row)
        }

        // all diagonals tl -> br / up right half
        for (c in 0..numCols - 1) {
            var str = ""
            var r2 = 0
            var c2 = c
            while (r2 < numLines && c2 < numCols) {
                str += input[r2][c2]
                ++r2
                ++c2
            }
            allForwardLines.add(str)
        }

        // all diagonals tl -> br | btm left half
        for (r in 1..numLines - 1) {
            var str = ""
            var r2 = r
            var c2 = 0
            while (r2 < numLines && c2 < numCols) {
                str += input[r2][c2]
                ++r2
                ++c2
            }
            allForwardLines.add(str)
        }

        // all diagonals tr -> bl
        for (c in numCols - 1 downTo 0) {
            var str = ""
            var r2 = 0
            var c2 = c
            while (r2 < numLines && c2 >= 0) {
                str += input[r2][c2]
                ++r2
                --c2
            }
            allForwardLines.add(str)
        }

        for (r in 1..numLines - 1) {
            var str = ""
            var r2 = r
            var c2 = numCols - 1
            while (r2 < numLines && c2 >= 0) {
                str += input[r2][c2]
                ++r2
                --c2
            }
            allForwardLines.add(str)

        }

        val allLines = buildList<String> {
            addAll(allForwardLines)
            allForwardLines.forEach { line ->
                add(line.reversed())
            }
        }

        val count = allLines.sumOf {
            it.windowed(xmas.length).count { it == xmas }
        }

        return count.toLong()
    }


    val mas = "MAS"

    fun part2(input: List<String>): Long {

        val numLines = input.size
        val numCols = input.first().length

        val pairs = mutableListOf<Pair<String, String>>()
        for (r in 0..numLines - mas.length) {
            for (c in 0..numCols - mas.length) {
                var str1 = ""
                var str2 = ""
                for (i in 0..mas.length - 1) {
                    str1 += input[r + i][c + i]
                    str2 += input[r + i][c + mas.length - 1 - i]
                }
                pairs.add(Pair(str1, str2))
            }
        }

        val count = pairs.count { p ->
            (p.first == mas || p.first.reversed() == mas) && (p.second == mas || p.second.reversed() == mas)
        }

        return count.toLong()
    }

    check(part1(listOf("XMAS")).also { it.println() } == 1L)
    check(part1(listOf("XMASAMX")).also { it.println() } == 2L)
    check(
        part1(
            listOf(
                "XXXX",
                "MXXX",
                "AXXX",
                "SXXX",
            )
        ).also { it.println() } == 1L
    )

    check(
        part1(
            listOf(
                "XXXX",
                "XMXX",
                "XXAX",
                "XXXS",
            )
        ).also { it.println() } == 1L
    )

    check(
        part1(
            listOf(
                "XXXS",
                "XMAX",
                "XMAX",
                "XXXS",
            )
        ).also { it.println() } == 2L)

    //check(part1(readInput("Day04_test_vg1")).also { it.println() } == 5L)
    // Test if implementation meets criteria from the description, like:
    val testInputVg2 = readInput("Day04_test_vg2")
    check(part1(testInputVg2).also { it.println() } == 2L)

    val testInput = readInput("Day04_test")
    check(testInput.size == 10)
    check(part1(testInput).also { it.println() } == 18L)
    check(part2(testInput).also { it.println() } == 9L)

    val input = readInput("Day04")
    check(input.size == 140)
    part1(input).println()
    part2(input).println()
}
