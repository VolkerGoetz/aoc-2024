sealed class Gate(val name: String, val in1: String, val in2: String, val out: String) {
    abstract fun execute(in1Val: Int, in2Val: Int): Int
    override fun toString() = "Gate($in1 $name $in2 -> $out)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Gate) return false

        if (name != other.name) return false
        if (in1 != other.in1) return false
        if (in2 != other.in2) return false
        if (out != other.out) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + in1.hashCode()
        result = 31 * result + in2.hashCode()
        result = 31 * result + out.hashCode()
        return result
    }

}

class And(in1: String, in2: String, out: String) : Gate("AND", in1, in2, out) {
    override fun execute(in1Val: Int, in2Val: Int) = in1Val and in2Val
}

class Or(in1: String, in2: String, out: String) : Gate("OR", in1, in2, out) {
    override fun execute(in1Val: Int, in2Val: Int) = in1Val or in2Val
}

class Xor(in1: String, in2: String, out: String) : Gate("XOR", in1, in2, out) {
    override fun execute(in1Val: Int, in2Val: Int) = in1Val xor in2Val
}

fun Gate(line: String): Gate {
    val parts = line.split(' ')
    // x00 AND y00 -> z00
    return when (parts[1]) {
        "AND" -> And(parts[0], parts[2], parts[4])
        "OR" -> Or(parts[0], parts[2], parts[4])
        "XOR" -> Xor(parts[0], parts[2], parts[4])
        else -> error("Unkown gate type ${parts[1]}")
    }
}

fun List<String>.wireStates() = buildMap {
    takeWhile { it.isNotBlank() }
        .map { l ->
            val (signal, value) = l.split("""\s*:\s""".toRegex())
            put(signal, value.toInt())
        }
}

fun List<String>.gates(): List<Gate> =
    takeLastWhile { it.isNotBlank() }
        .map { Gate(it) }

operator fun Map<String, Int>.contains(gate: Gate): Boolean = gate.in1 in this && gate.in2 in this

fun Map<String, Int>.toLong(prefix: String = "z") = this.keys
    .filter { it.matches("""$prefix\d\d""".toRegex()) }
    .sortedDescending()
    .map { this[it] }
    .joinToString("")
    .toLong(2)

fun evaluate(gates: List<Gate>, initialWireStates: Map<String, Int>): Map<String, Int> {
    val wireStates = initialWireStates.toMutableMap()
    val unprocessed = gates.toMutableList()
    while (unprocessed.isNotEmpty()) {
        val processed = mutableSetOf<Gate>()
        for (gate in unprocessed) {
            if (gate in wireStates) {
                wireStates[gate.out] = gate.execute(wireStates[gate.in1]!!, wireStates[gate.in2]!!)
                processed += gate
            }
        }
        if (processed.isEmpty()) {
            error("Cannont continue!")
        }
        unprocessed.removeAll(processed)
    }
    return wireStates
}

fun main() {

    fun part1(input: List<String>): Long {

        val initialWireStates = input.wireStates()
        //wireStates.forEach { it.println() }

        val gates = input.gates()
        //gates.forEach { it.println() }

        val wireStates = evaluate(gates, initialWireStates)
        val result = wireStates.keys
            .filter { it.matches("""z\d\d""".toRegex()) }
            .sortedDescending()
            .map { wireStates[it] }
            .joinToString("")
            .also { it.println() }
            .toLong(2)

        return result
    }

    val testInput1 = readInput("Day24_test1")
    check(testInput1.size == 10)
    check(part1(testInput1).also { it.println() } == 4L)
    //check(part2(testInput1).also { it.println() } == -1L)

    val testInput2 = readInput("Day24_test2")
    check(testInput2.size == 19)
    //check(part1(testInput2).also { it.println() } == 4L)
    //check(part2(testInput2).also { it.println() } == -1L)

    //val testInput = readInput("Day24_test")
    //check(testInput.size == 47)
    //check(part1(testInput).also { it.println() } == 2024L)
    ////check(part2(testInput1).also { it.println() } == -1L)

    val input = readInput("Day24")
    check(input.size == 313)
    //part1(input).println()
    //check(part1(input).also { it.println() } == 48063513640678L)
    //part2(input).println()
    //check(part2(input).also { it.println() } == -1L)
}

