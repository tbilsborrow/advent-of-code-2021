// returns strings of length 9 made up of the chars in the 3x3 window
// around each char in the input matrix, extending the convolution
// one char in every direction beyond the matrix (and treating those
// chars outside the matrix as [pad])
fun List<String>.convolve(pad: Char): Sequence<String> {
    return sequence {
        for (row in -1..this@convolve.size) {
            for (col in -1..this@convolve[0].length) {
                yield(IntRange(row - 1, row + 1).flatMap { r -> IntRange(col - 1, col + 1).map { c ->
                    if (r < 0 || r >= this@convolve.size || c < 0 || c >= this@convolve[r].length) {
                        pad
                    } else {
                        this@convolve[r][c]
                    }
                } }.joinToString(""))
            }
        }
    }
}

fun String.to9BitNum() = this.fold(0) { a, ch -> (a shl 1) + (if (ch == '#') 1 else 0) }

fun List<String>.enhance(pad: Char): List<String> =
    this.convolve(pad)
        .map { it.to9BitNum() }
        .map { algorithm[it] }
        .joinToString("")
        // convolved strings go 1 beyond the input in all directions
        // so each one is 2 wider (it's possible that the algorithm
        // results in a smaller bounding box of lit pixels, but I'm
        // not optimizing that away)
        .chunked(this.size + 2)

fun List<String>.numLit() = this.sumOf { s -> s.count { it == '#' } }

// if algo[0] is '.' then the infinite dark pixels stay dark
// otherwise algo[0] is '#' and infinite dark pixels all become lit
// if the last char in algo is '.' then infinite lit pixels become dark
// if algo[0] is '#' then algo[last] must be '.' otherwise there will
// always be infinite lit pixels
// This assumes if algo[0] is '#' then algo[last] must be '.', and the
// infinite pixels then flip between dark/lit every iteration
fun pad(algo: String, i: Int) = if (algo[0] == '.' || i % 2 == 1) '.' else '#'

val filename = "input-20.txt"
val input = java.io.File("../resources/$filename").readLines().filter { it.isNotBlank() }
val algorithm = input.first()
val image = input.drop(1).toList()

// part 1: 5503
println(IntRange(1, 2).fold(image) { a, i -> a.enhance(pad(algorithm, i)) }.numLit())

// part 2: 19156
println(IntRange(1, 50).fold(image) { a, i -> a.enhance(pad(algorithm, i)) }.numLit())
