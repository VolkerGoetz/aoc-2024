import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun List<String>.toIntList(nr: Int) = this.stream()
    .map { l -> l.split("\\s+".toRegex()) }
    .map { l -> l.get(nr) }
    .map { s -> s.toInt() }
    .toList()

fun List<String>.leftList() = this.toIntList(0)
fun List<String>.rightList() = this.toIntList(1)

