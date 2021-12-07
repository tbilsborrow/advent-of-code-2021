import kotlin.math.abs

val filename = "input-7.txt"

fun List<Int>.fuel(target: Int): Int = this.fold(0) { fuel, e -> fuel + (1..abs(e - target)).sum() }

val list = java.io.File("../resources/$filename").readText().trim()
    .split(",")
    .map { it.toInt() }
    .sorted()

// there's probably a fancy way to find the weighted median when the weights change
// depending on that median, but until that day comes when I can figure it out ...
// brutal force
var minFuel = Int.MAX_VALUE
for (i in (list[0]..list[list.size-1])) {
    val fuel = list.fuel(i)
    if (fuel < minFuel) minFuel = fuel
}

println(minFuel)
