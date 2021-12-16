val filename = "input-15.txt"
val riskMatrix = _2021_11.Matrix(java.io.File("../resources/$filename").readLines())

val MULTIPLIER = 5

// Looks a lot like part 1, except this has the concept of [MULTIPLIER]-1
// additional map copies ("virtual" tiles) in each direction, with the
// cost of each spot in each virtual tile modified according to the [cost] function.
// ALso I tried a different [adjacents] code style

data class Node(val row: Int, val col: Int, val cost: Int)

// at most (must be within the entire (including virtual tiles) matrix)
// 4 coords above, below, left, and right of (row,col)
// functional style:
// - start with cartesian product of all 9 nodes (including self) within 1
// - only consider nodes within the matrix
// - only consider vertically or horizontally aligned (no diagonals)
// - ignore self
fun _2021_11.Matrix.adjacents(row: Int, col: Int): List<Pair<Int, Int>> =
    IntRange(row - 1, row + 1).flatMap { r -> IntRange(col - 1, col + 1).map { c -> r to c } }
        .filter { it.first in 0 until this.n * MULTIPLIER && it.second in 0 until this.m * MULTIPLIER }
        .filter { it.first == row || it.second == col }
        .filterNot { it.first == row && it.second == col }
        .toList()

fun _2021_11.Matrix.cost(row: Int, col: Int): Int {
    val tileR = row % this.n
    val tileC = col % this.n
    // cost of the original tile, plus 1 for each virtual tile to the right/down that
    // (row,col) actually is in, circling back to 1 if virtual cost is over 9
    // (which is the transform to 0-based, then mod 9, then back to 1-based)
    return ((this[tileR][tileC] + (row / this.n) + (col / this.m) - 1) % 9) + 1
}

fun dijkstra(m: _2021_11.Matrix): Int {
    val visited = java.util.PriorityQueue<Node> { a, b -> a.cost - b.cost }
    val costMatrix = _2021_11.Matrix(riskMatrix.n * MULTIPLIER, riskMatrix.m * MULTIPLIER, Int.MAX_VALUE)

    visited.add(Node(0, 0, 0))
    costMatrix[0][0] = 0

    while(visited.isNotEmpty()) {
        val node = visited.poll()
        // early exit since I only want the one path
        if (node.row == m.n * MULTIPLIER - 1 && node.col == m.m * MULTIPLIER - 1) return costMatrix[node.row][node.col]

        for ((r,c) in m.adjacents(node.row, node.col)) {
            val cost = m.cost(r, c)
            if (costMatrix[node.row][node.col] + cost < costMatrix[r][c]) {
                costMatrix[r][c] = costMatrix[node.row][node.col] + cost
                visited.add(Node(r, c, costMatrix[r][c]))
            }
        }
    }
    return -1
}

// 2907
println(dijkstra(riskMatrix))
