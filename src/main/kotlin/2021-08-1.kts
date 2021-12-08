val filename = "input-08.txt"
val answer = java.io.File("../resources/$filename").readLines().asSequence()
    .map { it.split('|')[1].trim() }
    .map { it.split(' ') }
    .flatten()
    .filter { it.length in intArrayOf(2, 3, 4, 7) }
    .count()

println(answer)
