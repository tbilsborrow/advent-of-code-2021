sealed class Packet(val version: Int, val size: Int) {
    abstract fun versionSum(): Int
    abstract fun calculateValue(): Long
}

class LiteralPacket(version: Int, size: Int, private val value: Long): Packet(version, size) {
    override fun toString() = "v$version:s$size:$value"
    override fun versionSum(): Int = version
    override fun calculateValue(): Long = value
}

class OperatorPacket(version: Int, size: Int, private val type: Int, private val subpackets: List<Packet>): Packet(version, size) {
    override fun toString() = "v$version:s$size[${subpackets.joinToString(",") { it.toString() }}]"
    override fun versionSum(): Int = version + subpackets.sumOf { it.versionSum() }
    override fun calculateValue(): Long {
        return when (type) {
            0 -> subpackets.sumOf { it.calculateValue() }
            1 -> subpackets.map { it.calculateValue() }.reduce { a,v -> a * v }
            2 -> subpackets.minOf { it.calculateValue() }
            3 -> subpackets.maxOf { it.calculateValue() }
            5 -> if (subpackets[0].calculateValue() > subpackets[1].calculateValue()) 1 else 0
            6 -> if (subpackets[0].calculateValue() < subpackets[1].calculateValue()) 1 else 0
            7 -> if (subpackets[0].calculateValue() == subpackets[1].calculateValue()) 1 else 0
            else -> throw Exception("unexpected op type $type")
        }
    }
}

// Stateful bit traversal of the transmission
class Reader(private val transmission: String) {
    // current index into [transmission] - this will be the next char read
    private var pos = 0
    // bits extracted from transmission
    private var buffer: Int = 0
    // number of unread bits in [buffer]
    private var numBitsBuffered: Int = 0
    // total number of bits read from [transmission]
    var numBitsRead = 0

    // gets a number from the next n bits in the transmission
    fun get(n: Int): Int {
        // first buffer up enough bits (one hex char aka 4 bits at a time) to satisfy the request
        // assume never need to buffer more bits than will fit in an Int (256)
        while (n > numBitsBuffered) {
            if (pos >= transmission.length) throw Exception("attempted to read past end of transmission")
            buffer = (buffer shl 4) + transmission[pos].toString().toInt(16)
            numBitsBuffered += 4
            pos += 1
        }

        // desired value is made up of next n bits from the buffer
        val value = buffer shr (numBitsBuffered - n)

        // now clear the consumed bits from the buffer
        val mask = (1 shl (numBitsBuffered - n)) - 1
        buffer = buffer and mask
        numBitsBuffered -= n

        numBitsRead += n
        return value
    }
}

fun parseLiteral(reader: Reader, startingNumBits: Int, version: Int): LiteralPacket {
    var value: Long = 0
    do {
        val endflag = reader.get(1) == 0
        value = (value shl 4) + reader.get(4)
    } while (!endflag)
    val size = reader.numBitsRead - startingNumBits
    return LiteralPacket(version, size, value)
}

fun parseOperator(reader: Reader, startingNumBits: Int, version: Int, type: Int): OperatorPacket {
    val subpackets = mutableListOf<Packet>()

    val lengthType = reader.get(1)
    if (lengthType == 0) {
        val numBits = reader.get(15)
        var bitsRead = 0
        while (bitsRead < numBits) {
            val packet = parsePacket(reader)
            bitsRead += packet.size
            subpackets.add(packet)
        }
    } else {
        val numSubpackets = reader.get(11)
        repeat(numSubpackets) { subpackets.add(parsePacket(reader)) }
    }
    val size = reader.numBitsRead - startingNumBits
    return OperatorPacket(version, size, type, subpackets)
}

fun parsePacket(reader: Reader): Packet {
    val startingNumBits = reader.numBitsRead
    val version = reader.get(3)
    val type = reader.get(3)
    return if (type == 4) parseLiteral(reader, startingNumBits, version) else parseOperator(reader, startingNumBits, version, type)
}

fun test() {
    fun assertEquals(expected: Number, actual: Number) = if (expected.toLong() != actual.toLong()) throw Exception("fail $expected!=$actual") else {}
    
    assertEquals(2021, parsePacket(Reader("D2FE28")).calculateValue())
    assertEquals(6, parsePacket(Reader("D2FE28")).versionSum())
    assertEquals(16, parsePacket(Reader("8A004A801A8002F478")).versionSum())
    assertEquals(12, parsePacket(Reader("620080001611562C8802118E34")).versionSum())
    assertEquals(23, parsePacket(Reader("C0015000016115A2E0802F182340")).versionSum())
    assertEquals(31, parsePacket(Reader("A0016C880162017C3686B18A3D4780")).versionSum())

    assertEquals(3, parsePacket(Reader("C200B40A82")).calculateValue())
    assertEquals(54, parsePacket(Reader("04005AC33890")).calculateValue())
    assertEquals(7, parsePacket(Reader("880086C3E88112")).calculateValue())
    assertEquals(9, parsePacket(Reader("CE00C43D881120")).calculateValue())
    assertEquals(1, parsePacket(Reader("D8005AC2A8F0")).calculateValue())
    assertEquals(0, parsePacket(Reader("F600BC2D8F")).calculateValue())
    assertEquals(0, parsePacket(Reader("9C005AC2F8F0")).calculateValue())
    assertEquals(1, parsePacket(Reader("9C0141080250320F1802104A08")).calculateValue())
}
test()

val filename = "input-16.txt"
val transmission = java.io.File("../resources/$filename").readText().trim()

val reader = Reader(transmission)
val packet = parsePacket(reader)

// part 1: 879
println(packet.versionSum())

// part 2: 539051801941
println(packet.calculateValue())
