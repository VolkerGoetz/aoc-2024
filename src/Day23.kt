fun main() {

    fun List<String>.allPairsSingle() = map { it.substringBefore('-') to it.substringAfter('-') }
    fun List<Pair<String, String>>.allPairs() = this + map { it.second to it.first }
    fun List<String>.allPairs() = allPairsSingle().allPairs()

    fun List<String>.neighbours() = flatMap { l ->
        val f = l.substringBefore('-')
        val s = l.substringAfter('-')
        listOf(f to s, s to f)
    }.groupBy(keySelector = { it.first }, valueTransform = { it.second })

    fun part1(input: List<String>): Long {
        val allNeighbours = input.neighbours()
        println("allNeighbours: ${allNeighbours.size}")

        val santaTriplets: Set<List<String>> = buildSet {
            allNeighbours.keys
                .filter { it.startsWith('t') }
                .forEach { link1 ->
                    val l1neighs = allNeighbours[link1]!!.toList()
                    l1neighs.forEachIndexed { idx, link2 ->
                        l1neighs.drop(idx + 1).forEach { link3 ->
                            if (link2 in allNeighbours[link3]!!)
                                add(listOf(link1, link2, link3).sorted())
                        }
                    }
                }
        }
        //santaTriplets.sortedBy { it.joinToString("") }.forEach { it.joinToString(",").println() }
        //santaTriplets.size.println()

        return santaTriplets.size.toLong()
    }

    fun part2(input: List<String>): String {
        val allPairs = input.allPairs()

        fun List<String>.isClique() = flatMap { f -> map { s -> f to s } }
            .all { it.first == it.second || it.first to it.second in allPairs }

        val queue = input.neighbours().map { (it.value + it.key).sorted() }.toMutableList()
        val seen = mutableSetOf<List<String>>()
        var largestClique = emptyList<String>()

        while (queue.isNotEmpty()) {
            val possibleClique = queue.removeFirst()
            seen += possibleClique
            if (possibleClique.isClique()) {
                if (largestClique.size < possibleClique.size) {
                    largestClique = possibleClique
                    // shortcut all smaller possibilities
                    queue.removeAll { it.size <= largestClique.size }
                }
            } else {
                if (possibleClique.size - 1 > largestClique.size) {
                    queue.addAll(
                        possibleClique
                            .map { possibleClique - it }
                            .filter { it !in seen }
                            .filter { it !in queue }
                    )
                }
            }
        }

        return largestClique.sorted().joinToString(",")
    }

    val testInput = readInput("Day23_test")
    check(testInput.size == 32)
    check(part1(testInput).also { it.println() } == 7L)
    check(part2(testInput).also { it.println() } == "co,de,ka,ta")

    val input = readInput("Day23")
    check(input.size == 3380)
    //part1(input).println()
    check(part1(input).also { it.println() } == 1400L)
    //part2(input).println()
    check(part2(input).also { it.println() } == "am,bc,cz,dc,gy,hk,li,qf,th,tj,wf,xk,xo")
}
