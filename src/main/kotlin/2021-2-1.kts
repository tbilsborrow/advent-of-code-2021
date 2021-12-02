val lines = java.io.File("../resources/input-2.txt").readLines().asSequence()

data class State(
    val position: Int = 0,
    val depth: Int = 0,
) {
    fun forward(amount: Int) = State(position + amount, depth)
    fun down(amount: Int) = State(position, depth + amount)
    fun up(amount: Int) = State(position, depth - amount)
}

val r = lines.fold(State()) { state, s ->
    val (command, amount) = s.split(" ")
    when (command) {
        "forward" -> state.forward(amount.toInt())
        "down" -> state.down(amount.toInt())
        "up" -> state.up(amount.toInt())
        else -> throw IllegalArgumentException()
    }
}

println(r.position * r.depth)
