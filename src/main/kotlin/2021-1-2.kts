val lines = java.io.File("../resources/input-1.txt").readLines()

val r = lines
    .map { it.toInt() }
    .zipToTriples()
    .map { it.first + it.second + it.third }
    .zipWithNext()
    .count { it.second > it.first }

println(r)

fun <T> Iterable<T>.zipToTriples(): Sequence<Triple<T, T, T>> {
    return sequence result@ {
        val iterator = iterator()
        if (!iterator.hasNext()) return@result
        var a = iterator.next()
        if (!iterator.hasNext()) return@result
        var b = iterator.next()
        while (iterator.hasNext()) {
            val c = iterator.next()
            yield(Triple(a, b, c))
            a = b
            b = c
        }
    }
}
