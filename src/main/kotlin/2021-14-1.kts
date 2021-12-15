val filename = "input-14.txt"
val lines = java.io.File("../resources/$filename").readLines().filter { it.isNotEmpty() }

// The naive way: keep a list of characters, mutate with each generation
// by inserting a char between each pair of list elements

var polymer = lines[0].map { "$it" }

val rules = lines.subList(1, lines.size)
    .map { it.split(" -> ") }
    .associate { Pair(it[0], it[1]) }

fun <K> MutableMap<K, Long>.inc(key: K): MutableMap<K, Long> {
    this.merge(key, 1, Long::plus)
    return this
}

repeat(10) {
    val tuples = polymer.zipWithNext { a,b -> Triple(a, rules["$a$b"]!!, b) }
    polymer = tuples.fold(listOf(tuples[0].first)) { acc, t -> acc + t.second + t.third }
}

// then get counts from the whole list
val counts = polymer.fold(mutableMapOf<String, Long>()) { counters, element -> counters.inc(element) }

val max = counts.maxOf { it.value }
val min = counts.minOf { it.value }
println(max - min)
