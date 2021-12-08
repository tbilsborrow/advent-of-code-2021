val filename = "input-08.txt"
val answer = java.io.File("../resources/$filename").readLines().asSequence()
    .map { it.split('|') }
    .map { Pair(it[0].trim().split(' '), it[1].trim().split(' '))} // pair of signals, codes
    .map { Pair(deduce(it.first), it.second) } // pair of segment mapping, list of codes
    .map { (mapping, codes) -> findValue(codes, mapping) }
    .sum()

println(answer)

// --------------------------------------------------------

/**
 * Figure out which segment (0-6) is represented by which letter (a-g)
 *    000
 *   1   2
 *    333
 *   4   5
 *    666
 *
 * @return map of letter -> segment number
 */
fun deduce(signal: List<String>): Map<Char, Int> {
    // map of segment number -> possible letter codes
    // algorithm:
    // - start with all segments with the potential to be any letter
    // - remove letters from each potential until they all have only one left
    val potential = (0..6).associateWith { "abcdefg" }.toMutableMap()

    // part 1: remove codes based on signal
    for (s in signal) {
        when (s.length) {
            2 -> keep(s, potential, 2, 5)        // it's a 1 so must be in segments 2,5
            3 -> keep(s, potential, 0, 2, 5)     // it's a 7 so must be in segments 0,2,5
            4 -> keep(s, potential, 1, 2, 3, 5)  // it's a 4 so must be in segments 1,2,3,5
            5 -> keep(s, potential, 0, 3, 6)     // it's 2/3/5 which all cover segments 0,3,6
            6 -> keep(s, potential, 0, 1, 5, 6)  // it's 0/6/9 which all cover segments 0,1,5,6
        }
    }

    // part 2: remove codes from other potentials when a potential has only one possibility left
    // build decoder map going the other way, from only possible letter -> segment number
    val decoder = mutableMapOf<Char, Int>()
    while (potential.isNotEmpty()) {
        val entry = potential.entries.first { it.value.length == 1 }
        // found a potential with only one possibility
        potential.remove(entry.key)
        decoder[entry.value[0]] = entry.key
        // remove that possibility from the rest
        potential.forEach { potential[it.key] = potential[it.key]!!.filterNot { v -> v == entry.value[0] } }
    }

    return decoder
}

// for all potentials identified by segments, keep only letters in s
fun keep(s: String, potential: MutableMap<Int, String>, vararg segments: Int) {
    for (segment in segments) potential[segment] = potential[segment]!!.filter { it in s }
}

// code is a string of segment letters, map each to a segment number and
// determine what digit is lit up with those segments
fun findDigit(code: String, segmentMapping: Map<Char, Int>): Int {
    return when (val x = code.map { segmentMapping[it]!! }.sorted().joinToString("")) {
        "25" -> 1
        "02346" -> 2
        "02356" -> 3
        "1235" -> 4
        "01356" -> 5
        "013456" -> 6
        "025" -> 7
        "0123456" -> 8
        "012356" -> 9
        "012456" -> 0
        else -> throw Exception("unexpected segment list $x")
    }
}

// each code is one digit, put digits together to get a number
fun findValue(codes: List<String>, segmentMapping: Map<Char, Int>): Int =
    codes.map { findDigit(it, segmentMapping) }.joinToString("").toInt()
