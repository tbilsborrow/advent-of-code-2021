val TOKENS = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
val VALUES = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
val SCORES = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

// return pair of invalid char, completion string
fun String.check(): Pair<Char?, String> {
    val stack = java.util.ArrayDeque<Char>()
    this.forEach {
        if (TOKENS.containsKey(it)) {
            stack.push(TOKENS[it]!!)
        } else {
            val ch = stack.pop()
            if (ch != it) return Pair(it, stack.completionString())
        }
    }
    return Pair(null, stack.completionString())
}

// terser and maybe cooler but I think less understandable
fun String.checkRecursive(acc: String = ""): Pair<Char?, String> {
    if (this.isEmpty()) return Pair(null, acc)
    if (TOKENS.containsKey(this[0])) return this.substring(1).checkRecursive(TOKENS[this[0]]!! + acc)
    if (this[0] != acc.first()) return Pair(this[0], acc)
    return this.substring(1).checkRecursive(acc.substring(1))
}

fun Char?.value() = VALUES[this] ?: 0

fun java.util.ArrayDeque<Char>.completionString() = this.joinToString("")

fun String.score(): Long = this.fold(0) { acc, ch -> acc * 5 + SCORES[ch]!! }

// --------------------------------------------------------

val filename = "input-10.txt"
val input = java.io.File("../resources/$filename").readLines().asSequence()

val part1 = input
    .map { it.check() }
    .map { it.first.value() }
    .sum()

// 167379
println(part1)

val part2 = input
    .map { it.check() }
    .filter { it.first == null }
    .map { it.second.score() }
    .sorted()
    .toList()

// 2776842859
println(part2[part2.size / 2])
