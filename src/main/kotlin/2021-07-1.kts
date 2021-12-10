import kotlin.math.abs

val filename = "input-07.txt"

// if there's an evenly sized list, fuel calc is the same result
// when picking anything between (inclusive) of the two middle elements
// so just use the same index as for an oddly size list
fun List<Int>.closeEnoughMedian(): Int {
    val l = this.sorted()
    return l[l.size / 2]
}

fun List<Int>.fuel(target: Int): Int = this.fold(0) { fuel, e -> fuel + abs(e - target) }

val list = java.io.File("../resources/$filename").readText().trim()
    .split(",")
    .map { it.toInt() }

val median = list.closeEnoughMedian()
val fuel = list.fuel(median)

println(fuel)
