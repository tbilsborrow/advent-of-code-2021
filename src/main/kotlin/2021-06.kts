val filename = "input-6.txt"

// map of [timer value] -> [number of fish at that timer value]
val state = java.io.File("../resources/$filename").readText().trim()
    .split(",")
    .map { it.toInt() }
    .fold(mutableMapOf<Int, Long>()) { counters, num ->
        counters.merge(num, 1, Long::plus)
        counters
    }

fun countFish(initialState: Map<Int, Long>, numGenerations: Int): Long {
    var state = initialState
    for (gen in (1..numGenerations)) {
        val nextGen = mutableMapOf<Int, Long>()
        state.forEach {
            // key is timer, value is fish count
            val nextTimer = if (it.key == 0) {
                // fish at the end of their timer create that many new fish
                nextGen[8] = it.value
                // and also reset their own timer
                6
            } else {
                // otherwise all fish at this timer move to the next time decrement
                it.key - 1
            }
            // really the only need for a merge is to handle fish at both 0 and 7 moving to 6;
            // coulda called that out with an `if` but whatever
            nextGen.merge(nextTimer, it.value, Long::plus)
        }
        state = nextGen
    }

    return state.values.sum()
}

println("Part 1: ${countFish(state, 80)}")
println("Part 2: ${countFish(state, 256)}")
