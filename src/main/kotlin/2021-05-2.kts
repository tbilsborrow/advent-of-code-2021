val lines = java.io.File("../resources/input-5.txt").readLines().asSequence()

lines
    .map { _2021_05_1.Line(it) }
    .fold(mutableMapOf<_2021_05_1.Point, Int>()) { counters, line ->
        line.forEach { point -> counters.merge(point, 1, Int::plus) }
        counters
    }
    .filter { it.value > 1 }
    .count()
