val lines = java.io.File("input-03.txt").readLines()

fun List<String>.rating(bigger: Boolean, index: Int = 0): Int {
    // assumes lines are all the same length
    val (ones, zeroes) = this.partition { it[index] == '1' }
    val l = if ((ones.size >= zeroes.size) == bigger) ones else zeroes
    // assumes we're going to get down to 1 before index >= line length
    if (l.size == 1) return Integer.parseInt(l.first(), 2)
    return l.rating(bigger, index + 1)
}

val orating = lines.rating(true)
val co2rating = lines.rating(false)

println(orating * co2rating)
