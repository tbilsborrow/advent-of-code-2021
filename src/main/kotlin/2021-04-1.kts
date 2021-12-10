import kotlin.system.exitProcess

class Board(lines: List<String>) {
    private val cells: Array<IntArray>
    private val rowSums: IntArray
    private val colSums: IntArray
    var isWinner = false

    init {
        val size = lines.size
        // assume all boards are square
        cells = Array(size) { IntArray(size) }
        rowSums = IntArray(size)
        colSums = IntArray(size)
        lines.forEachIndexed { col, line ->
            line.trim().split(" +".toRegex()).forEachIndexed { row, entry ->
                cells[row][col] = entry.toInt()
                rowSums[row] += entry.toInt()
                colSums[col] += entry.toInt()
            }
        }
    }

    // returns -1 if board is not a winner, non-negative score if board is a winner
    // assumes board contains only non-negative entries
    fun play(number: Int): Int {
        for (row in cells.indices) {
            for (col in cells.indices) {
                if (cells[row][col] == number) {
                    // assumes number exists at most once per board
                    cells[row][col] = -1
                    rowSums[row] -= number
                    colSums[col] -= number

                    // number found and board is a winner
                    if (rowSums[row] == 0 || colSums[col] == 0) {
                        isWinner = true
                        return score(number)
                    }

                    // number found but board not a winner
                    return -1
                }
            }
        }
        // number not found
        return -1
    }

    private fun score(number: Int): Int {
        return number * rowSums.sum()
    }
}

// can't import functions from a kotlin script so this gets to be a class
class Input {
    fun load(filename: String): Pair<List<Int>, List<Board>> {
        val lines = java.io.File(filename).readLines()

        val plays = lines[0].split(",").map { it.toInt() }

        val size = lines[2].trim().split(" +".toRegex()).size
        val boards = lines.drop(2).filter { it.isNotBlank() }.chunked(size).map { Board(it) }

        return Pair(plays, boards)
    }
}

val (plays, boards) = Input().load("input-04.txt")

for (number in plays) {
    for (board in boards) {
        val score = board.play(number)
        if (board.isWinner) {
            println(score)
            exitProcess(0)
        }
    }
}
