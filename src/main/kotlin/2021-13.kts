val filename = "input-13.txt"

data class Point(val x: Int, val y: Int) {
    // assumes folded points always end up on the page (ie row >= half of max point.y
    fun foldY(row: Int) = Point(x, row - (y - row))
    // assumes folded points always end up on the page (ie col >= half of max point.x
    fun foldX(col: Int) = Point(col - (x - col), y)
}

data class Instruction(val isHorizontal: Boolean, val value: Int) {
    fun fold(point: Point) =
        if (isHorizontal && point.y > value) point.foldY(value)
        else if (!isHorizontal && point.x > value) point.foldX(value)
        else point
}

val (points, instrs) = java.io.File("../resources/$filename").readLines()
    .filter { it.isNotEmpty() }
    .partition { !it.startsWith("fold along") }

val grid = points.map {
    val (x, y) = it.split(",")
    Point(x.toInt(), y.toInt())
}.toSet()

val folds = instrs.map {
    val (dir, value) = it.substring("fold along ".length).split("=")
    Instruction(dir == "y", value.toInt())
}

fun Set<Point>.fold(instruction: Instruction): Set<Point> {
    return this.map { instruction.fold(it) }.toSet()
}

fun Set<Point>.toPrintableRows(): List<String> {
    val maxX = this.maxOf { it.x }
    val maxY = this.maxOf { it.y }
    val grid = Array(maxY+1) { CharArray(maxX+1) { ' ' } }
    this.forEach { grid[it.y][it.x] = '#' }
    return grid.map { it.joinToString("") }
}

// part 1: 737
println(grid.fold(folds[0]).size)

// part 2: ZUJUAFHP
val folded = folds.fold(grid) { g, instr -> g.fold(instr) }
folded.toPrintableRows().forEach { println(it) }
