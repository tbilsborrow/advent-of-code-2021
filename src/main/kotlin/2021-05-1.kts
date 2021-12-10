data class Point(val x: Int, val y: Int)

class Line(line: String) : Sequence<Point> {
    private val start: Point
    private val end: Point

    init {
        // assume pretty specific input format
        val coords = line.split(" -> ", ",").map { it.toInt() }
        start = Point(coords[0], coords[1])
        end = Point(coords[2], coords[3])
    }

    fun isHorizontal() = start.y == end.y
    fun isVertical() = start.x == end.x

    override fun iterator() : Iterator<Point> =
        if (isHorizontal()) {
            val step = if (start.x < end.x) 1 else -1
            IntProgression.fromClosedRange(start.x, end.x, step).map { Point(it, start.y) }.iterator()
        } else if (isVertical()) {
            val step = if (start.y < end.y) 1 else -1
            IntProgression.fromClosedRange(start.y, end.y, step).map { Point(start.x, it) }.iterator()
        } else {
            // assume only 45 degree diagonal lines exist
            val xstep = if (start.x < end.x) 1 else -1
            val ystep = if (start.y < end.y) 1 else -1
            IntProgression.fromClosedRange(start.x, end.x, xstep).mapIndexed { i, x ->
                Point(x, start.y + i * ystep)
            }.iterator()
        }
}

val lines = java.io.File("input-05.txt").readLines().asSequence()

lines
    .map { Line(it) }
    .filter { it.isHorizontal() || it.isVertical() }
    .fold(mutableMapOf<Point, Int>()) { counters, line ->
        line.forEach { point -> counters.merge(point, 1, Int::plus) }
        counters
    }
    .filter { it.value > 1 }
    .count()
