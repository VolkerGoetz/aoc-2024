sealed class KeyLocks(val pinKeys: List<Int>) {
    override fun toString(): String {
        return "${this.javaClass.name}($pinKeys)"
    }
}

class Lock(pinKeys: List<Int>) : KeyLocks(pinKeys)
class Key(pinKeys: List<Int>) : KeyLocks(pinKeys)

fun Lock.matches(key: Key) =
    pinKeys.zip(key.pinKeys).all { it.first + it.second <= 5 }

fun main() {

    fun part1(input: List<String>): Long {
        val keyLocks = input.chunked(8)
            .map { it.take(7) }
            .map { thing ->
                val isLock = thing.first().equals("#####")
                val pinKeys = mutableListOf(0, 0, 0, 0, 0)
                for (l in thing.slice(1..5)) {
                    for (r in 0..4) {
                        if (l[r] == '#') {
                            pinKeys[r] = pinKeys[r] + 1
                        }
                    }
                }
                if (isLock) {
                    Lock(pinKeys)
                } else {
                    Key(pinKeys)
                }
            }

        val locks = keyLocks.filter { it is Lock }.map { it as Lock }
        val keys = keyLocks.filter { it is Key }.map { it as Key }

        val matchingPairs = locks.flatMap { lock ->
            keys.map { key ->
                Pair(lock, key) to lock.matches(key)
            }
        }

        return matchingPairs.count() { it.second }.toLong()
    }

    val testInput = readInput("Day25_test")
    check(testInput.size == 39)
    check(part1(testInput).also { it.println() } == 3L)

    val input = readInput("Day25")
    check(input.size == 3999)
    //part1(input).println()
    check(part1(input).also { it.println() } == 3671L)
}

