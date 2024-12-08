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


data class Point(val x: Int, val y: Int)

infix fun Int.to(that: Int): Point = Point(this, that)
fun Pair<Point, Point>.vector(): Point =
    Point(second.x - first.x, second.y - first.y)

operator fun Point.plus(that: Point): Point =
    Point(this.x + that.x, this.y + that.y)

class Grid() : ArrayList<ArrayList<Char>>() {

    constructor(lines: List<String>) : this() {
        for (x in lines[0].indices) {
            var col = ArrayList<Char>()
            for (y in lines.indices) {
                col.add(lines[y][x])
            }
            add(col)
        }
    }

    val allPoints by lazy {
        indices.flatMap { x ->
            this[x].indices.map { y -> Point(x, y) }
        }
    }

    operator fun get(pos: Point) = this[pos.x][pos.y]

    operator fun contains(p: Point) =
        p.x in this.indices && p.y in this[0].indices
}
