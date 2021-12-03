val lines = java.io.File("../resources/input-3.txt").readLines()

val numBits = lines.first().length
var gamma = 0
var epsilon = 0
for (i in 0 until numBits) {
    // assumes lines are all the same length
    val countOnes = lines.count { it[i] == '1' }
    gamma = gamma shl 1
    epsilon = epsilon shl 1
    if (countOnes > lines.count() / 2) gamma = gamma or 1 else epsilon = epsilon or 1
}

println(gamma * epsilon)
