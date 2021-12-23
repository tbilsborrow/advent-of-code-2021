import kotlin.math.abs

// keep everything relative to scanner 0
// (using the orientation of scanner 0 and as if scanner 0 is at coordinates 0,0,0)

data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "($x,$y,$z)"
}

data class Scanner(
    val name: String,
    var origin: Point? = null,
    var orientation: _2021_11.Matrix? = null,
    var points: MutableSet<Point> = mutableSetOf()
) {
    fun isAligned() = origin != null
}

data class ScannerMeta(val origin: Point, val orientation: _2021_11.Matrix)

// --------------------------------------------------------
// point and matrix helpers

operator fun Point.plus(p: Point): Point = Point(this.x + p.x, this.y + p.y, this.z + p.z)
operator fun Point.minus(p: Point): Point = Point(this.x - p.x, this.y - p.y, this.z - p.z)
fun Point.manhattan(p: Point) = abs(this.x - p.x) + abs(this.y - p.y) + abs(this.z - p.z)

operator fun _2021_11.Matrix.times(other: _2021_11.Matrix): _2021_11.Matrix {
    val product = _2021_11.Matrix(other.n, other.m)
    for (i in 0 until this.n) {
        for (j in 0 until other.m) {
            for (k in 0 until this.m) {
                product[i][j] += this[i][k] * other[k][j]
            }
        }
    }
    return product
}

operator fun _2021_11.Matrix.times(point: Point): Point {
    val other = _2021_11.Matrix(3, 1)
    other[0][0] = point.x
    other[1][0] = point.y
    other[2][0] = point.z
    val product = this * other
    return Point(product[0][0], product[1][0], product[2][0])
}

// --------------------------------------------------------
// rotation matrices

val identity = _2021_11.Matrix("1 0 0", "0 1 0", "0 0 1")
val rx90 = _2021_11.Matrix("1 0 0", "0 0 -1", "0 1 0")
val ry90 = _2021_11.Matrix("0 0 1", "0 1 0", "-1 0 0")
val rz90 = _2021_11.Matrix("0 -1 0", "1 0 0", "0 0 1")

// a sequence of all 24 possible orientations
// (6 directions to face, 4 rotations to roll for each direction)
val orientations: Sequence<_2021_11.Matrix> = sequence {
    fun rotations(base: _2021_11.Matrix, rot: _2021_11.Matrix): Sequence<_2021_11.Matrix> = sequence {
        IntRange(1, 4).fold(base) { acc, _ -> yield(acc); acc * rot }
    }

    // start by facing out along the x axis
    // 1-4 around x axis
    rotations(identity, rx90).forEach { yield(it) }
    // 5-8 y90 then around z axis
    rotations(ry90, rz90).forEach { yield(it) }
    // 9-12 y180 then around x axis
    rotations(ry90 * ry90, rx90).forEach { yield(it) }
    // 13-16 y270 then around z axis
    rotations(ry90 * ry90 * ry90, rz90).forEach { yield(it) }
    // 17-20 z90 then around y axis
    rotations(rz90, ry90).forEach { yield(it) }
    // 21-24 z270 then around y axis
    rotations(rz90 * rz90 * rz90, ry90).forEach { yield(it) }
}

// --------------------------------------------------------
// utils

fun String.toPoint(): Point? {
    val a = this.split(",").mapNotNull { it.toIntOrNull() }
    if (a.size != 3) return null
    return Point(a[0], a[1], a[2])
}

fun <T> List<T>.allPairs(bothDirections: Boolean = false): Sequence<Pair<T, T>> = sequence {
    for (i in 0 until this@allPairs.size-1) {
        for (j in i + 1 until this@allPairs.size) {
            yield(this@allPairs[i] to this@allPairs[j])
            if (bothDirections) yield(this@allPairs[j] to this@allPairs[i])
        }
    }
}

// --------------------------------------------------------
// actual problem logic

// Visually (easier for me to imagine in 2D but concept applies):
// pick up other set of points, rotate and/or flip them in all possible
// orientations and try to place them on top of this set of points such
// that enough of them are right on top of each other.
//
// try all points in this set (call the current candidate point (a1))
//  translate all this's points to an origin of (a1)
//  try all possible orientations
//   transform all points in other to current orientation
//   try all points in other (call the current candidate point (a2))
//    translate all other's (reoriented) points to origin of (a2)
//    at this point, they might be in the same orientation, and (a1)
//        might actually be the same physical point as (a2),
//    if [howMuch] are in both sets, it means the current orientation
//        is the transform needed to put the other points in this's
//        orientation, and (a1) is in fact (a2)
// this's original origin translates to -(a1)
// other's original origin translates to -(a2)
// other's origin wrt this's original origin and orientation is (a1)-(a2)
// returns a matrix and a point (or null if these two scanners don't overlap):
//  matrix = orientation to apply to other to bring it in the same space as this
//  point = origin of other with respect to this's origin and orientation
fun Scanner.overlaps(other: Scanner, howMuch: Int = 12): ScannerMeta? {
    // so inefficient
    this.points.forEach { a1 -> // a1 is in this's orientation
        val translated = this.points.map { it - a1 }
        orientations.forEach { o ->
            other.points.forEach { p -> // p is in other's orientation
                val a2 = o * p // a2 is in this's orientation
                // these are all in this's orientation
                val otherTranslated = other.points.map { o * it }.map { it - a2 }.toSet()
                val overlapping = translated.intersect(otherTranslated)
                if (overlapping.size >= howMuch) return@overlaps ScannerMeta(a1 - a2, o)
            }
        }
    }
    return null
}

// Naughty!! Mutates all overlapping scanners:
// - sets origin and orientation of the first scanner to (0,0,0) and identity matrix respectively
//   as this is the space in which we want to put al other scanners
// - for all other scanners that are found to overlap:
//   - sets origin to the scanner's origin wrt scanners[0]
//   - sets orientation to the transform needed to align the scanner with scanners[0]
//   - transforms all points to be in this orientation
// Once this completes, any scanner with null orientation or origin could not be
// found to overlap with any other scanner
fun align(scanners: List<Scanner>) {
    scanners[0].orientation = identity
    scanners[0].origin = Point(0, 0, 0)
    var foundMatch = true
    var round = 0
    var placedCount = 1
    while (foundMatch) {
        println("round ${++round}")
        foundMatch = false
        scanners.allPairs(bothDirections = true).forEach {
            if (it.first.isAligned() && !it.second.isAligned()) {
                val meta = it.first.overlaps(it.second)
                if (meta != null) {
                    // second overlapped first - mutation ahoy
                    it.second.orientation = meta.orientation
                    it.second.origin = meta.origin + it.first.origin!!
                    it.second.points = it.second.points.map { p -> meta.orientation * p }.toMutableSet()

                    foundMatch = true
                    println(" placed ${it.second.name} off of ${it.first.name} with origin ${it.first.origin} (overlaps origin ${meta.origin})")
                    placedCount++
                }
            }
        }
    }
    println("placed $placedCount out of ${scanners.size} scanners")
}

// --------------------------------------------------------

val filename = "input-19.txt"
val scanners = mutableListOf<Scanner>()
var currentScanner: Scanner? = null
java.io.File("../resources/$filename").readLines().filter { it.isNotBlank() }.forEach {
    val point = it.toPoint()
    if (point == null) {
        currentScanner = Scanner(it)
        scanners.add(currentScanner!!)
    } else {
        currentScanner!!.points.add(point)
    }
}

align(scanners)

// gather all distinct beacons, translated to be from scanners[0]'s point of view
val beacons = scanners.fold(setOf<Point>()) { acc, scanner -> acc + scanner.points.map { p -> scanner.origin!! + p } }

// part 1: 342
println("# of beacons: ${beacons.size}")

// part 2: 9668
println("max distance: ${scanners.allPairs().maxOf { it.first.origin!!.manhattan(it.second.origin!!) }}")
