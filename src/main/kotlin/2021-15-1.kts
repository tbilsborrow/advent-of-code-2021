val filename = "input-15.txt"
val riskMatrix = _2021_11.Matrix(java.io.File("../resources/$filename").readLines())

data class Node(val row: Int, val col: Int, val cost: Int)

// at most (must be within the matrix) 4 coords above, below, left, and right of (row,col)
fun _2021_11.Matrix.adjacents(row: Int, col: Int): List<Pair<Int, Int>> {
    val l = mutableListOf<Pair<Int, Int>>()
    if (row > 0) l.add(Pair(row-1, col))
    if (row < this.n - 1) l.add(Pair(row+1, col))
    if (col > 0) l.add(Pair(row, col-1))
    if (col < this.m - 1) l.add(Pair(row, col+1))
    return l
}

fun dijkstra(m: _2021_11.Matrix): Int {
    val visited = java.util.PriorityQueue<Node> { a, b -> a.cost - b.cost }
    val costMatrix = _2021_11.Matrix(riskMatrix.n, riskMatrix.m, Int.MAX_VALUE)

    visited.add(Node(0, 0, 0))
    costMatrix[0][0] = 0

    while(visited.isNotEmpty()) {
        val node = visited.poll()
        // early exit since I only want the one path
        if (node.row == m.n - 1 && node.col == m.m - 1) return costMatrix[node.row][node.col]

        for ((r,c) in m.adjacents(node.row, node.col)) {
            if (costMatrix[node.row][node.col] + m[r][c] < costMatrix[r][c]) {
                costMatrix[r][c] = costMatrix[node.row][node.col] + m[r][c]
                visited.add(Node(r, c, costMatrix[r][c]))
            }
        }
    }
    return -1
}

// aww - the least cost path in the puzzle input isn't the classic "down and to the right"
//fun dp(m: _2021_11.Matrix): Int {
//    val costMatrix = _2021_11.Matrix(riskMatrix.n, riskMatrix.m)
//    for (r in 0 until m.n) {
//        for (c in 0 until m.m) {
//            if (r == 0 && c == 0) costMatrix[r][c] = 0
//            else if (r == 0) costMatrix[r][c] = costMatrix[r][c-1] + m[r][c]
//            else if (c == 0) costMatrix[r][c] = costMatrix[r-1][c] + m[r][c]
//            else costMatrix[r][c] = kotlin.math.min(costMatrix[r][c-1], costMatrix[r-1][c]) + m[r][c]
//        }
//    }
//    return costMatrix[m.n-1][m.m-1]
//}

// 604
println(dijkstra(riskMatrix))
