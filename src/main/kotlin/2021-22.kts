data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "($x,$y,$z)"
}

data class Cuboid(val p1: Point, val p2: Point, val on: Boolean = true) {
    fun volume(): Long = (p2.x - p1.x + 1L) * (p2.y - p1.y + 1) * (p2.z - p1.z + 1)

    fun overlaps(c: Cuboid): Boolean =
        c.p1.x <= p2.x && c.p2.x >= p1.x &&
                c.p1.y <= p2.y && c.p2.y >= p1.y &&
                c.p1.z <= p2.z && c.p2.z >= p1.z

    private fun splitx(x: Int): Pair<Cuboid, Cuboid>? =
        if (x > this.p1.x && x <= this.p2.x) Pair(
            Cuboid(Point(this.p1.x, this.p1.y, this.p1.z), Point(x - 1, this.p2.y, this.p2.z)),
            Cuboid(Point(x, this.p1.y, this.p1.z), Point(this.p2.x, this.p2.y, this.p2.z)),
        )
        else null

    private fun splity(y: Int): Pair<Cuboid, Cuboid>? =
        if (y > this.p1.y && y <= this.p2.y) Pair(
            Cuboid(Point(this.p1.x, this.p1.y, this.p1.z), Point(this.p2.x, y - 1, this.p2.z)),
            Cuboid(Point(this.p1.x, y, this.p1.z), Point(this.p2.x, this.p2.y, this.p2.z)),
        )
        else null

    private fun splitz(z: Int): Pair<Cuboid, Cuboid>? =
        if (z > this.p1.z && z <= this.p2.z) Pair(
            Cuboid(Point(this.p1.x, this.p1.y, this.p1.z), Point(this.p2.x, this.p2.y, z - 1)),
            Cuboid(Point(this.p1.x, this.p1.y, z), Point(this.p2.x, this.p2.y, this.p2.z)),
        )
        else null

    // remove any overlap - splitting this as necessary such that this with
    // the overlap removed becomes multiple cuboids whose combined shape
    // matches the desired (non-cuboidal) shape
    operator fun minus(c: Cuboid): List<Cuboid> {
        if (!this.overlaps(c)) return listOf(this)

        // Every split that actually does something will result
        // in an henceforth unaffected part and a working part.
        // The unaffected part is added to the result, the working
        // part is potentially further split.
        // The split returns a [Pair], which part of that pair is
        // the unaffected part vs the working part depends on the
        // "direction" of the split - if splitting an axis at the
        // lower of c's points, unaffected is first, working is second;
        // if splitting at the upper of c's points, the pair is reversed.
        // Yes this is crying out for code rework.
        val ret = mutableListOf<Cuboid>()
        var workingCuboid = this
        var p = workingCuboid.splitx(c.p1.x)
        if (p != null) { ret += p.first; workingCuboid = p.second }
        p = workingCuboid.splitx(c.p2.x + 1)
        if (p != null) { ret += p.second; workingCuboid = p.first }
        p = workingCuboid.splity(c.p1.y)
        if (p != null) { ret += p.first; workingCuboid = p.second }
        p = workingCuboid.splity(c.p2.y + 1)
        if (p != null) { ret += p.second; workingCuboid = p.first }
        p = workingCuboid.splitz(c.p1.z)
        if (p != null) { ret += p.first; workingCuboid = p.second }
        p = workingCuboid.splitz(c.p2.z + 1)
        if (p != null) { ret += p.second }
        return ret
    }
}

// For each new cuboid, remove overlap from all existing cuboids.
// If the new cuboid is "on", it gets added in full.
fun List<Cuboid>.doStep(newCuboid: Cuboid): List<Cuboid> {
    val newList = this.fold(mutableListOf<Cuboid>()) { a, c -> a.addAll(c - newCuboid); a }
    if (newCuboid.on) newList += newCuboid
    return newList
}

fun input(filename: String): List<Cuboid> {
    val re = Regex("(\\bon\\b|\\boff\\b) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")

    fun buildCuboid(s: String): Cuboid {
        val values = re.find(s)?.groupValues!!
        return Cuboid(
            Point(values[2].toInt(), values[4].toInt(), values[6].toInt()),
            Point(values[3].toInt(), values[5].toInt(), values[7].toInt()),
            values[1] == "on",
        )
    }

    val lines = java.io.File("../resources/$filename").readLines()
    return lines.map { buildCuboid(it) }
}

val cuboids = input("input-22.txt")

// part 1: 583641
val part1Region = Cuboid(Point(-50, -50, -50), Point(50, 50, 50))
val part1Cuboids = cuboids.filter { it.overlaps(part1Region) }
val initialization = part1Cuboids.fold(listOf<Cuboid>()) { a, c -> a.doStep(c) }
println("part 1) ${initialization.sumOf { it.volume() }} (using ${initialization.size} cuboids)")

// part 2: 1182153534186233
val core = cuboids.fold(listOf<Cuboid>()) { a, c -> a.doStep(c) }
println("part 2) ${core.sumOf { it.volume() }} (using ${core.size} cuboids)")
