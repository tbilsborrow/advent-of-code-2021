val filename = "input-14.txt"
val lines = java.io.File("../resources/$filename").readLines().filter { it.isNotEmpty() }

// The part 2 way: keep a map of pairs -> number of occurrences.
// For each generation, make a new map by taking an original pair,
// creating two new pairs resulting from the original pair's rule
// (ie if the rule is AB->C, then all AB become AC and CB), and
// adding the number of occurrences of the original pair to any
// existing instances of the new pairs (ie take AB's count and add
// it to any other new AC and CB).

var polymer = lines[0].map { "$it" }.zipWithNext { a,b -> "$a$b"}.associateWith { 1L }.toMutableMap()

val rules = lines.subList(1, lines.size)
    .map { it.split(" -> ") }
    .associate { Pair(it[0], it[1]) }

fun <K> MutableMap<K, Long>.inc(key: K, amount: Long = 1): MutableMap<K, Long> {
    this.merge(key, amount, Long::plus)
    return this
}

repeat(40) {
    val next = mutableMapOf<String, Long>()
    polymer.forEach {
        val e = rules[it.key]!!
        next.inc("${it.key[0]}$e", it.value)
        next.inc("$e${it.key[1]}", it.value)
    }
    polymer = next
}

// turns out polymer order doesn't matter, just need to count the first char of each pair
// (because the second char will be counted as the first char in some other pair)
val counts = polymer.entries.fold(mutableMapOf<String, Long>()) { c, e -> c.inc(e.key[0].toString(), e.value) }
// except for the last char in the original polymer, it will always remain last
// and won't be the first char in any other pair, so need to account for it here
counts.inc(lines[0].last().toString())

val max = counts.maxOf { it.value }
val min = counts.minOf { it.value }
println(max - min)
