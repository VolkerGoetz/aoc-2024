fun main() {

    class ChronospatialComputer(input: List<String>) {

        val initRegA = input.findLineContent("Register A:").toLong()
        val initRegB = input.findLineContent("Register B:").toLong()
        val initRegC = input.findLineContent("Register C:").toLong()
        var regA = initRegA
        var regB = initRegB
        var regC = initRegC
        var pc = 0
        val instructions = input.findLineContent("Program:").split(',').map { it.toLong() }
        val output = mutableListOf<Long>()

        init {
            //printState()
        }

        fun run() {
            while (runStep()) {
                //printState()
            }
        }

        fun runWithRegAValue(newRegA: Long) {
            regA = newRegA
            run()
        }

        fun isOutEqualsInstructions() = output == instructions

        fun getOutput() = output.joinToString(",")

        private fun runStep(): Boolean {

            if (pc >= instructions.size) {
                return false
            }
            val instr = instructions[pc]
            val op = instructions[pc + 1]

            pc = when (instr) {
                0L -> adv(op)
                1L -> bxl(op)
                2L -> bst(op)
                3L -> jnz(op)
                4L -> bxc(op)
                5L -> out(op)
                6L -> bdv(op)
                7L -> cdv(op)
                else -> error("Illegal instruction $instr")
            }

            return true
        }

        private fun debugOp(content: String) {
            //println(content)
        }

        // 0
        private fun adv(op: Long): Int {
            // regA / 2^cOp <-> regA shr cOp
            debugOp("adv: regA = $regA shr ${getComboOperand(op).toInt()}")
            regA = regA shr getComboOperand(op).toInt()
            return pc + 2
        }

        // 1
        private fun bxl(op: Long): Int {
            // regB XOR lOp
            debugOp("bxl: regB = $regB XOR $op")
            regB = regB.xor(op)
            return pc + 2
        }

        // 2
        private fun bst(op: Long): Int {
            // combo Op mod 8
            debugOp("bst: regB = cop($op = ${getComboOperand(op)}) % 8")
            regB = getComboOperand(op) % 8
            return pc + 2
        }

        // 3
        private fun jnz(op: Long): Int {
            debugOp("jnz: $regA == 0 ? ${pc + 2} : $op")
            //error("stop on JNZ")
            //printState()
            return if (regA == 0L) pc + 2 else op.toInt()
        }

        // 4
        private fun bxc(op: Long): Int {
            debugOp("bxc: $regB XOR $regC")
            regB = regB.xor(regC)
            return pc + 2
        }

        // 5
        private fun out(op: Long): Int {
            debugOp("---------------------> out: cop($op = ${getComboOperand(op)} % 8")
            output += getComboOperand(op) % 8
            return pc + 2
        }

        // 6
        private fun bdv(op: Long): Int {
            debugOp("bdv: regB = $regA shr ${getComboOperand(op).toInt()}")
            regB = regA shr getComboOperand(op).toInt()
            return pc + 2
        }

        // 7
        private fun cdv(op: Long): Int {
            debugOp("cdv: regC = $regA shr ${getComboOperand(op).toInt()}")
            regC = regA shr getComboOperand(op).toInt()
            return pc + 2
        }

        private fun getComboOperand(op: Long) =
            when (op) {
                in 0..3 -> op
                4L -> regA
                5L -> regB
                6L -> regC
                else -> error("Illegal combo operand $op")
            }


        private fun List<String>.findLineContent(prefix: String) =
            this.first { it.startsWith(prefix) }.substringAfter(':').trim()

        fun printState() {
            println("Register A=$regA | B=$regB | C=$regC")
            val regAbin = regA.toString(2)
            println("regA len=${regAbin.length}")
            println("regA: $regAbin")
            println("PC=$pc")
            println("PRG: $instructions")
            println("OUT: $output")
            kotlin.io.println()
        }

        fun validate(
            regAshould: Long?,
            regBshould: Long?,
            regCshould: Long?,
            pcShould: Int?,
        ): Boolean {
            return regAshould?.let { regAshould == regA } != false
                    && regBshould?.let { regBshould == regB } != false
                    && regCshould?.let { regCshould == regC } != false
                    && pcShould?.let { pcShould == pc } != false
        }

        fun reset() {
            regA = initRegA
            regB = initRegB
            regC = initRegC
            pc = 0
            output.clear()
        }

        fun reverseEngineer(): Long {
            val queue = mutableListOf("" to 1)
            var iters = 0
            while (queue.isNotEmpty()) {
                (++iters).println()
                val (prefix, tailSize) = queue.removeFirst()
                if (tailSize > instructions.size) return prefix.toLong(8)

                for (digit in 0..7) {
                    reset()
                    val tryPrefix = "$prefix$digit"
                    runWithRegAValue(tryPrefix.toLong(8))
                    if (output == instructions.takeLast(tailSize)) {
                        queue.add(tryPrefix to tailSize + 1)
                    }
                }
            }
            return 0L
        }
    }

    fun part1(input: List<String>) =
        ChronospatialComputer(input).apply {
            run()
        }.getOutput()

    fun part2(input: List<String>): Long {

        val startValue = ChronospatialComputer(input).reverseEngineer()
        ChronospatialComputer(input).apply {
            runWithRegAValue(startValue)
            check(isOutEqualsInstructions())
        }
        return startValue
    }

    val testInputVg1 = readInput("Day17_test_vg_1")
    check(testInputVg1.size == 5)
    check(part1(testInputVg1).also { it.println() } == "")

    val testInputVg2 = readInput("Day17_test_vg_2")
    check(testInputVg2.size == 5)
    check(part1(testInputVg2).also { it.println() } == "0,1,2")

    val testInputVg3 = readInput("Day17_test_vg_3")
    check(testInputVg3.size == 5)
    check(part1(testInputVg3).also { it.println() } == "4,2,5,6,7,7,7,7,3,1,0")

    val testInputVg4 = readInput("Day17_test_vg_4")
    check(testInputVg4.size == 5)
    check(part1(testInputVg4).also { it.println() } == "")

    val testInputVg5 = readInput("Day17_test_vg_5")
    check(testInputVg5.size == 5)
    check(part1(testInputVg5).also { it.println() } == "")

    val testInput1 = readInput("Day17_test_1")
    check(testInput1.size == 5)
    check(part1(testInput1).also { it.println() } == "4,6,3,5,6,3,5,2,1,0")
    //check(part2(testInput).also { it.println() } == "")

    val testInput2 = readInput("Day17_test_2")
    check(testInput2.size == 5)
    check(part1(testInput2).also { it.println() } == "5,7,3,0")
    check(part2(testInput2).also { it.println() } == 117440L)

    val input = readInput("Day17")
    check(input.size == 5)
    ////part1(input).println()
    check(part1(input).also { it.println() } == "3,1,4,3,1,7,1,6,3")
    //part2(input).println()
    check(part2(input).also { it.println() } > 0L)
}
