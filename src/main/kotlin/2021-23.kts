import java.lang.StringBuilder
import kotlin.math.abs

val energyMap = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)

fun String.replace(index: Int, ch: Char): String = substring(0, index) + ch + substring(index + 1)

data class Room(val location: Int, val type: Char, val occupants: String) {
    fun isFilled(fromIndex: Int = 0) = occupants.substring(fromIndex).all { it == type }
    fun isAccepting(type: Char) = (type == this.type) && occupants.none { it != type && it != '.' }
    fun numSpotsAvailable() = occupants.count { it == '.' }
    fun removeAmphipod(index: Int) = Room(location, type, occupants.replace(index, '.'))
    fun addAmphipod() = Room(location, type, occupants.replace(occupants.lastIndexOf('.'), type))
}

data class Hallway(val rooms: List<Room>, var occupants: String = "...........") {
    fun isClear(fromIndex: Int, toIndex: Int) =
        if (fromIndex < toIndex) occupants.substring(fromIndex + 1, toIndex + 1).all { it == '.' }
        else occupants.substring(toIndex, fromIndex).all { it == '.' }
}

data class Burrow(val hallway: Hallway, val cost: Int = 0) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Cost: $cost\n")
        sb.append("\n".padStart(hallway.occupants.length + 3, '#'))
        sb.append("#" + hallway.occupants + "#\n")
        val w = (hallway.occupants.length - (hallway.rooms.size * 2 - 1)) / 2
        var fill = '#'
        for (roomPos in hallway.rooms[0].occupants.indices) {
            sb.append("".padEnd(w, fill))
            for (room in hallway.rooms) sb.append("#" + room.occupants[roomPos])
            sb.append("#")
            sb.append("\n".padStart(w+1, fill))
            fill = ' '
        }
        sb.append("".padEnd(w, fill))
        sb.append("".padEnd(hallway.rooms.size * 2 + 1, '#'))
        return sb.toString()
    }

    fun isComplete() = hallway.rooms.all { it.isFilled() }

    private fun moveFromRoomToHallway(room: Room, roomPos: Int, hallPos: Int): Burrow {
        val type = room.occupants[roomPos]
        val additionalCost = (roomPos + 1 + abs(hallPos - room.location)) * energyMap[type]!!
        val newRooms = hallway.rooms.map { if (it.location == room.location) it.removeAmphipod(roomPos) else it.copy() }
        val newHallway = Hallway(newRooms, hallway.occupants.replace(hallPos, type))
        return Burrow(newHallway, cost + additionalCost)
    }

    private fun moveFromHallwayToRoom(room: Room, hallPos: Int): Burrow {
        val additionalCost = (room.numSpotsAvailable() + abs(hallPos - room.location)) * energyMap[room.type]!!
        val newRooms = hallway.rooms.map { if (it.location == room.location) it.addAmphipod() else it.copy() }
        val newHallway = Hallway(newRooms, hallway.occupants.replace(hallPos, '.'))
        return Burrow(newHallway, cost + additionalCost)
    }

    // all states that are possible by moving one amphipod to somewhere else on the map
    fun reachableStates(): List<Burrow> {
        val states = mutableListOf<Burrow>()

        // moving from room to hallway
        for (room in hallway.rooms) {
            for (i in room.occupants.indices) {
                if (room.occupants[i] == '.') continue
                if (!room.isFilled(i)) {
                    // move right
                    for (h in room.location + 1 until hallway.occupants.length) {
                        // until someone else is blocking the hallway
                        if (hallway.occupants[h] != '.') break
                        // can't stop in front of a room
                        if (h in hallway.rooms.map { it.location }) continue
                        states.add(moveFromRoomToHallway(room, i, h))
                    }
                    // move left
                    for (h in room.location - 1 downTo 0) {
                        if (hallway.occupants[h] != '.') break
                        if (h in hallway.rooms.map { it.location }) continue
                        states.add(moveFromRoomToHallway(room, i, h))
                    }
                }
                break
            }
        }

        // moving from hallway to room
        for (i in hallway.occupants.indices) {
            if (hallway.occupants[i] == '.') continue
            for (room in hallway.rooms) {
                if (room.isAccepting(hallway.occupants[i]) && hallway.isClear(i, room.location)) {
                    states.add(moveFromHallwayToRoom(room, i))
                    break
                }
            }
        }

        return states
    }
}

fun findMinEnergy(burrow: Burrow): Int {
    val toCheck = java.util.PriorityQueue<Burrow> { a, b -> a.cost - b.cost }
    val checked = mutableMapOf<Burrow, Int>()

    toCheck.add(burrow)

    while (toCheck.isNotEmpty()) {
        val state = toCheck.poll()
        if (state.isComplete()) continue
        // brute force getting cost for every possible state
        for (next in state.reachableStates()) {
            val prevCost = checked[next]
            if (prevCost == null || next.cost < prevCost) {
                checked[next] = next.cost
                toCheck.add(next)
            }
        }
    }
    return checked.keys.filter { it.isComplete() }.minOf { it.cost }
}

val burrowPart1 = Burrow(Hallway(rooms = listOf(
    Room(2, 'A', "CD"),
    Room(4, 'B', "AD"),
    Room(6, 'C', "BB"),
    Room(8, 'D', "CA"),
)))
// 18300
println("part 1: ${findMinEnergy(burrowPart1)}")

val burrowPart2 = Burrow(Hallway(rooms = listOf(
    Room(2, 'A', "CDDD"),
    Room(4, 'B', "ACBD"),
    Room(6, 'C', "BBAB"),
    Room(8, 'D', "CACA"),
)))
// 50190
println("part 2: ${findMinEnergy(burrowPart2)}")
