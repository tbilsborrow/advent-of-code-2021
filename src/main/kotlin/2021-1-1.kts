val lines = java.io.File("../resources/input-1.txt").readLines()

val r = lines
    .map { it.toInt() }
    .zipWithNext()
    .count { it.second > it.first }

println(r)
