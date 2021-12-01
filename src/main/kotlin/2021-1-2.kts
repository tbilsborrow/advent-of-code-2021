val lines = java.io.File("../resources/input-1.txt").readLines().asSequence()

val r = lines
    .map { it.toInt() }
    .windowed(3)
    .map { it.sum() }
    .zipWithNext()
    .count { it.second > it.first }

println(r)
