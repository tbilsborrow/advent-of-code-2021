data class Player(var pos: Int, var score: Int = 0)

//val players = listOf(Player(4), Player(8)) // TEST
val players = listOf(Player(2), Player(1))

fun <T> Iterator<T>.asIterable() : Iterable<T> = object : Iterable<T> {
    private val iter = this@asIterable
    override fun iterator() = iter
}

// kind of unexpected way to get the sequence/iterator to remember its position https://stackoverflow.com/a/56579834
fun seq(max: Int) = generateSequence(1) { if (it == max) 1 else it + 1 }.iterator().asIterable()

// simple naive direct iterative data point tracking
fun playDeterministic(players: List<Player>): Int {
    val p = players.map { it.copy() }
    val die = seq(100)
    var turn = 0
    var numRolls = 0

    while (p[0].score < 1000 && p[1].score < 1000) {
        val move = die.take(3).sum()
        p[turn].pos = ((p[turn].pos + move - 1) % 10) + 1
        p[turn].score += p[turn].pos
        numRolls += 3
        turn = 1 - turn
    }

    return p.minOf { it.score } * numRolls
}

// 3 rolls adding up to "key" results in "value" universes
val universes = mapOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)

// play all distinct universes, return a list containing the number
// of universes in which each player won
fun playQuantum(players: List<Player>, turn: Int = 0): List<Long> {
    if (players[0].score >= 21) return listOf(1, 0)
    if (players[1].score >= 21) return listOf(0, 1)
    // nobody won yet, back to the multiverse
    var list = listOf(0L, 0L)
    for (u in universes) {
        // using the call stack as my cache of player data along the way
        val p = players.map { it.copy() }
        p[turn].pos = ((p[turn].pos + u.key - 1) % 10) + 1
        p[turn].score += p[turn].pos
        val x = playQuantum(p, 1 - turn)
        list = listOf(list[0] + (x[0] * u.value), list[1] + (x[1] * u.value))
    }
    return list
}

// part 1: loser score (730) * num rolls (1092) = 797160
println("part 1) ${playDeterministic(players)}")

// part 2: 27464148626406 (player 2 won only 22909380722959)
println("part 2) ${playQuantum(players).maxOf { it }}")
