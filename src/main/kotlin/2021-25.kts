class Grid(private val n: Int, private val m: Int = n, private val initial: Char = '.') {
    constructor(lines: List<String>) : this(lines.size, lines[0].length) {
        for (r in (0 until n)) {
            for (c in (0 until m)) {
                d[r][c] = lines[r][c]
            }
        }
    }

    private val d = Array(n) { CharArray(m) { initial } }
    private operator fun get(i: Int) = d[i]
    override fun toString() =
        d.joinToString("\n") {
            it.joinToString("")
        }

    fun move(): Boolean {
        var numMoved = 0

        // east herd simultaneously to new grid
        val east = Grid(this.n, this.m)
        for (i in 0 until n) {
            for (j in 0 until m) {
                if (this[i][j] == '>') {
                    if (this[i][(j + 1) % m] == '.') {
                        east[i][(j + 1) % m] = '>'
                        numMoved++
                    } else {
                        east[i][j] = '>'
                    }
                } else if (this[i][j] == 'v') {
                    east[i][j] = 'v'
                }
            }
        }

        // clear self to receive south herd update
        for (i in 0 until n) {
            for (j in 0 until m) {
                this[i][j] = '.'
            }
        }

        // south herd simultaneously back into this grid
        for (i in 0 until n) {
            for (j in 0 until m) {
                if (east[i][j] == 'v') {
                    if (east[(i + 1) % n][j] == '.') {
                        this[(i + 1) % n][j] = 'v'
                        numMoved++
                    } else {
                        this[i][j] = 'v'
                    }
                } else if (east[i][j] == '>') {
                    this[i][j] = '>'
                }
            }
        }

        return numMoved > 0
    }
}

fun input(filename: String): List<String> = java.io.File("../resources/$filename").readLines()

val grid = Grid(input("input-25.txt"))

var stepNum = 1
while (grid.move()) stepNum++
println(stepNum)
