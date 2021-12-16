// general use N x M integer matrix
open class Matrix(val n: Int, val m: Int, private val initial: Int = 0) {
    constructor(lines: List<String>) : this(lines.size, lines[0].length) {
        for (r in (0 until n)) {
            for (c in (0 until m)) {
                d[r][c] = lines[r][c].toString().toInt()
            }
        }
    }
    private val d = Array(n) { IntArray(m) { initial } }
    operator fun get(i: Int) = d[i]
    override fun toString() =
        d.joinToString("\n") {
            it.joinToString(" ")
        }
}

class Octopi(lines: List<String>) : Matrix(lines) {
    var numFlashes = 0

    private fun inc(r: Int, c: Int) {
        if (this[r][c] >= 0) {
            this[r][c]++
            if (this[r][c] > 9) this.flash(r, c)
        }
    }

    private fun flash(r: Int, c: Int) {
        numFlashes++
        this[r][c] = -1
        for (pr in (r-1..r+1)) {
            for (pc in (c-1..c+1)) {
                if (pr in 0 until n && pc in 0 until m) this.inc(pr, pc)
            }
        }
    }

    fun step(num: Int = 1) {
        repeat(num) {
            // everyone gets incremented (which can result in flash chains)
            for (r in (0 until this.n)) {
                for (c in (0 until this.m)) {
                    this.inc(r, c)
                }
            }

            // everyone flashed gets reset
            for (r in (0 until this.n)) {
                for (c in (0 until this.m)) {
                    if (this[r][c] == -1) this[r][c] = 0
                }
            }
        }
    }

    fun findSync(): Int {
        var gen = 0
        while(true) {
            val prevFlashCount = numFlashes
            this.step()
            gen++
            // assumes this will actually happen reasonably quickly
            if (numFlashes == prevFlashCount + (n * m)) return gen
        }
    }
}

val filename = "input-11.txt"

// part 1: 1669
val octopi = Octopi(java.io.File("../resources/$filename").readLines())
octopi.step(100)
println(octopi.numFlashes)

// part 2: 351
val octopi2 = Octopi(java.io.File("../resources/$filename").readLines())
println(octopi2.findSync())
