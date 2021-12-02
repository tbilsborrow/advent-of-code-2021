val lines = java.io.File("../resources/input-2.txt").readLines().asSequence()

data class State(
    val position: Int = 0,
    val depth: Int = 0,
    val aim: Int = 0,
) {
    fun forward(amount: Int) = State(position + amount, depth + (aim * amount), aim)
    fun down(amount: Int) = State(position, depth, aim + amount)
    fun up(amount: Int) = State(position, depth, aim - amount)
}

val ops = mapOf(
    "forward" to State::forward,
    "down" to State::down,
    "up" to State::up,
)

val r = lines.fold(State()) { state, s ->
    val (command, amount) = s.split(" ")
    ops[command]!!.invoke(state, amount.toInt())
}

println(r.position * r.depth)
