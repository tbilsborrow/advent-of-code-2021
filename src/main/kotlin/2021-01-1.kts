val lines = java.io.File("input-01.txt").readLines().asSequence()

val r = lines
    .map { it.toInt() }
    .zipWithNext()
    .count { it.second > it.first }

println(r)
