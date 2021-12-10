val filename = "input-09.txt"
val heightmap = java.io.File("../resources/$filename").readLines()
    .map { line -> line.map { it.toString().toInt() } }

// is the height in this direction lower
fun List<List<Int>>.north(x: Int, y: Int): Boolean = y > 0 && this[y-1][x] <= this[y][x]
fun List<List<Int>>.south(x: Int, y: Int): Boolean = y < this.size-1 && this[y+1][x] <= this[y][x]
fun List<List<Int>>.east(x: Int, y: Int): Boolean = x < this[y].size-1 && this[y][x+1] <= this[y][x]
fun List<List<Int>>.west(x: Int, y: Int): Boolean = x > 0 && this[y][x-1] <= this[y][x]

val minima = mutableListOf<Int>()
for (y in heightmap.indices) {
    for (x in heightmap[y].indices) {
        if (heightmap.north(x, y)) continue
        if (heightmap.south(x, y)) continue
        if (heightmap.east(x, y)) continue
        if (heightmap.west(x, y)) continue
        minima.add(heightmap[y][x])
    }
}

// 603
println(minima.sumOf { it + 1 })
