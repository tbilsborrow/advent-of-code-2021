val filename = "input-09.txt"
// multidimensional array setup in kotlin is not pretty
val heightmap = java.io.File("../resources/$filename").readLines()
    .map { line -> line.map { it.toString().toInt() } }

val basinmap = heightmap.map { it.map { -1 }.toMutableList() }
val basinSizes = mutableMapOf<Int, Int>()
var nextBasinId = 0

// ------------------------------------
// impure utilities

fun createBasin(row: Int, col: Int) {
    basinmap[row][col] = nextBasinId
    basinSizes[nextBasinId] = 1
    nextBasinId++
}

fun joinBasin(row: Int, col: Int, basin: Int) {
    basinmap[row][col] = basin
    basinSizes[basin] = basinSizes[basin]!! + 1
}

// merge b1 into b2
fun mergeBasins(b1: Int, b2: Int) {
    basinmap.forEach { line ->
        for (c in line.indices) if (line[c] == b1) line[c] = b2
    }
    basinSizes[b2] = basinSizes[b2]!! + basinSizes[b1]!!
    basinSizes.remove(b1)
}

// ------------------------------------

for (row in heightmap.indices) {
    for (col in heightmap[row].indices) {
        // super important puzzle declaration:
        //   Locations of height 9 do not count as being in any basin,
        //   and all other locations will always be part of exactly one basin.
        // so heightmap 1234321 is invalid puzzle input as the 4 must belong
        // to exactly one basin and therefore cannot be a boundary
        // so a basin can only have a 9 as its boundaries
        if (heightmap[row][col] == 9) continue
        val northBasin = if (row == 0) -1 else basinmap[row-1][col]
        val westBasin = if (col == 0) -1 else basinmap[row][col-1]
        if (northBasin == -1 && westBasin == -1) {
            createBasin(row, col)
        } else if (northBasin == -1 && westBasin != -1) {
            joinBasin(row, col, westBasin)
        } else if (northBasin != -1 && westBasin == -1) {
            joinBasin(row, col, northBasin)
        } else {
            joinBasin(row, col, northBasin)
            if (northBasin != westBasin) mergeBasins(westBasin, northBasin)
        }
    }
}

// 786780
val answer = basinSizes.values.sorted().reversed().take(3).reduce(Int::times)
println(answer)
