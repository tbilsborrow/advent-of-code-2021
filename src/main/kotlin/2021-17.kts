// part 1: 25200

// No code, just examination:
// - assumes the target is completely below the y=0 line
// - with positive initial y velocity, the y positions on the way up are repeated on the way down
// - this means at some point the probe will again be at y=0, and with y velocity equal to negative initial y velocity
// - so the next step will put the probe at y=-(initial velocity + 1)
// - we want this next step to hit the bottom edge of the target (to maximize initial velocity and therefore height)
// - so max initial y velocity is -targetBottom - 1 (224 for the puzzle input)
// - max height then is 224+223+222+...+1, which, as everyone knows, for an even number is (n+1)*(n/2) = 25200
// - x velocity doesn't even matter!

// --------------------------------------------------------
// part 2

data class Rect(val top: Int, val right: Int, val bottom: Int, val left: Int) {
    fun contains(x: Int, y: Int) = y in bottom..top && x in left..right
}

val target = Rect(-177, 65, -225, 32)

val minx = 8                     // minimum n where sum(1..n) > target.left
val maxx = target.right
val miny = target.bottom
val maxy = -target.bottom - 1    // assume negative target.bottom

// return whether or not the probe landed in the target at any step
fun shoot(vx: Int, vy: Int, target: Rect): Boolean {
    var x = 0; var y = 0
    var dx = vx; var dy = vy
    // if it has gone past the target, it's all over
    while(x <= target.right && y >= target.bottom) {
        if (target.contains(x, y)) return true
        x += if (dx > 0) dx-- else 0
        y += dy--
    }
    return false
}

// shoot all combinations between min/max x/y, see which ones land
// part 2: 3012
IntRange(minx, maxx).flatMap { x -> IntRange(miny, maxy).map { y -> x to y } }
    .map { shoot(it.first, it.second, target) }
    .count { it }
