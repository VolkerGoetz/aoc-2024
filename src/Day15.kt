sealed interface WarehouseObject
data object Free : WarehouseObject
data object Wall : WarehouseObject
data object Box : WarehouseObject
data object BigBoxL : WarehouseObject
data object BigBoxR : WarehouseObject
data object Robot : WarehouseObject

fun WarehouseObject(type: Char) =
    when (type) {
        '.' -> Free
        '#' -> Wall
        'O' -> Box
        '[' -> BigBoxL
        ']' -> BigBoxR
        '@' -> Robot
        else -> error("Unknown object type $type")
    }

fun WarehouseObject.isBox() = this == Box || this == BigBoxL || this == BigBoxR

enum class Moves(val direction: Direction) {
    Up(Direction.North),
    Down(Direction.South),
    Right(Direction.East),
    Left(Direction.West),
}

fun Moves(direction: Char) =
    when (direction) {
        '^' -> Moves.Up
        'v' -> Moves.Down
        '>' -> Moves.Right
        '<' -> Moves.Left
        else -> error("Unknown movement $direction")
    }

class Warehouse : Grid<WarehouseObject> {

    val big: Boolean
    var currentPos: Point

    constructor(plan: List<String>, big: Boolean) : super(plan, ::WarehouseObject) {
        this.big = big
        currentPos = allPoints.first { get(it) == Robot }
    }

    override fun toString(): String = buildString {
        for (y in 0..<gridSize.y) {
            for (x in 0..<gridSize.x) {
                when (get(Point(x, y))) {
                    Free -> "."
                    Wall -> "#"
                    Box -> "O"
                    BigBoxL -> "["
                    BigBoxR -> "]"
                    Robot -> "@"
                }.let { append(it) }
            }
            append("\n")
        }
    }

    fun collectNextBoxesLinear(startPos: Point, direction: Direction): List<Point> {

        return buildList {
            add(startPos)
            var nextPos = startPos + direction
            while (get(nextPos).isBox()) {
                add(nextPos)
                nextPos += direction
            }
        }.reversed()
    }

    fun collectBigBoxesTree(startPos: Point, direction: Direction): List<Point> {

        return buildList {
            val type = get(startPos)
            if (type.isBox()) {
                addAll(collectBigBoxesTree(startPos + direction, direction))
                var secondBox: Point?
                if (type == BigBoxL) {
                    secondBox = startPos + Moves.Right.direction
                    addAll(collectBigBoxesTree(secondBox + direction, direction))
                } else {
                    secondBox = startPos + Moves.Left.direction
                    addAll(collectBigBoxesTree(secondBox + direction, direction))
                }
                add(startPos)
                add(secondBox)
            }
        }
    }

    fun moveRobot(move: Moves) {

        val boxes = mutableListOf<Point>()
        var nextPos = currentPos + move.direction
        var canMove = false
        val nextObject = get(nextPos)
        when (nextObject) {
            Free -> canMove = true
            Wall -> canMove = false
            Box -> {
                if (big) error("Illegal state")
                boxes.addAll(collectNextBoxesLinear(nextPos, move.direction))
                canMove = boxes.all { get(it + move.direction).let { t -> t.isBox() || t == Free } }
            }

            BigBoxL, BigBoxR -> {
                if (!big) error("Illegal state")
                if (move.direction.dy == 0) {
                    boxes.addAll(collectNextBoxesLinear(nextPos, move.direction))
                } else {
                    val startPoint = if (nextObject == BigBoxL) nextPos else nextPos + Moves.Left.direction
                    boxes.addAll(collectBigBoxesTree(startPoint, move.direction).distinct())
                }
                canMove = boxes.all { get(it + move.direction).let { t -> t.isBox() || t == Free } }
            }

            else -> error("Illegal state")
        }

        if (canMove) {
            boxes.forEach {
                set(it + move.direction, get(it))
                set(it, Free)
            }
            set(currentPos, Free)
            currentPos = currentPos + move.direction
            set(currentPos, Robot)
        }
    }

    fun allBoxPositions() =
        allPoints.filter { get(it).let { (big && it == BigBoxL) || (!big && it == Box) } }

}

fun WarehouseSmall(lines: List<String>) =
    lines.takeWhile { !it.isEmpty() }
        .let { Warehouse(it, false) }

fun WarehouseBig(lines: List<String>) =
    lines.takeWhile { !it.isEmpty() }
        .map { l ->
            l.map { c ->
                when (c) {
                    '#' -> "##"
                    'O' -> "[]"
                    '.' -> ".."
                    '@' -> "@."
                    else -> ""
                }
            }.joinToString("")
        }.let {
            Warehouse(it, true)
        }

fun movments(lines: List<String>) = lines
    .takeLastWhile { !it.isEmpty() }
    .joinToString("")
    .map { Moves(it) }

fun main() {

    fun Point.gpsPos() = x + 100 * y

    fun part12(input: List<String>, big: Boolean = false): Long {
        val wh = if (big) WarehouseBig(input) else WarehouseSmall(input)
        val mov = movments(input)

        mov.forEach {
            wh.moveRobot(it)
        }

        return wh.allBoxPositions().sumOf { it.gpsPos() }.toLong()
    }

    val testInput1 = readInput("Day15_test_1")
    check(testInput1.size == 10)
    check(part12(testInput1).also { it.println() } > 0L)

    val testInput = readInput("Day15_test")
    check(testInput.size == 21)
    check(part12(testInput).also { it.println() } == 10092L)
    check(part12(testInput, true).also { it.println() } == 9021L)

    val input = readInput("Day15")
    check(input.size == 71)
    //part1(input).println()
    check(part12(input).also { it.println() } == 1492518L)
    //part2(input).println()
    check(part12(input, true).also { it.println() } == 1512860L)
}
