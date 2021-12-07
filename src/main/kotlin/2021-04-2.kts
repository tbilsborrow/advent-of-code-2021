import kotlin.system.exitProcess

val (plays, boards) = _2021_04_1.Input().load("../resources/input-4.txt")

var numNonWinningBoards = boards.size
for (number in plays) {
    for (board in boards) {
        if (!board.isWinner) {
            val score = board.play(number)
            if (board.isWinner) {
                numNonWinningBoards--
                if (numNonWinningBoards == 0) {
                    // that was the last board
                    println(score)
                    exitProcess(0)
                }
            }
        }
    }
}
