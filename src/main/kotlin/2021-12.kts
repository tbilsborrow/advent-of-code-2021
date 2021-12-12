val filename = "input-12.txt"

val graph = mutableMapOf<String, MutableList<String>>()

java.io.File("../resources/$filename").readLines().forEach {
    val (src, dst) = it.split(("-"))
    graph.computeIfAbsent(src) { mutableListOf() }.add(dst)
    graph.computeIfAbsent(dst) { mutableListOf() }.add(src)
}

fun String.isLowerCase() = this.filterNot { it in 'a'..'z' }.isEmpty()
fun <T> Set<T>.addIf(value: T, f: () -> Boolean): Set<T> = if (f()) this + value else this

interface Constraint {
    // if visiting this cave leads to an invalid path
    fun deadend(cave: String): Boolean
    // return new constraint state after having visited the given cave
    fun visit(cave: String): Constraint
}

class Constraint1(private val visited: Set<String> = setOf()) : Constraint {
    override fun deadend(cave: String) = visited.contains(cave)
    override fun visit(cave: String) = Constraint1(visited.addIf(cave) { cave.isLowerCase() })
}

class Constraint2(private val visited: Set<String> = setOf(), private val somewhereTwice: Boolean = false) : Constraint {
    override fun deadend(cave: String) = visited.contains(cave) && (cave == "start" || somewhereTwice)
    override fun visit(cave: String) = Constraint2(
        visited.addIf(cave) { cave.isLowerCase() },
        if (visited.contains(cave)) true else somewhereTwice
    )
}

// return the number of paths through [graph] starting at [src], with the given contraints
fun countPaths(src: String, constraint: Constraint): Int {
    if (src == "end") return 1
    if (constraint.deadend(src)) return 0
    val v = constraint.visit(src)
    return graph[src]!!.sumOf { countPaths(it, v) }
}

// part 1: 4773
println(countPaths("start", Constraint1()))

// part 2: 116985
println(countPaths("start", Constraint2()))
