enum class Dir { Left, Right }

data class Node(
    var parent: Node?,
    var side: Dir?, // which sibling am I, left or right (or null at the root)
    var leftChild: Node? = null,
    var rightChild: Node? = null,
    var value: Int? = null
) {
    override fun toString(): String =
        if (!isNumber()) "[${leftChild},${rightChild}]"
        else value?.toString() ?: ""

    fun isRoot() = this.parent == null
    fun isNumber() = value != null
    fun incNumber(n: Int) { value = value!! + n }

    // if both children are number nodes
    fun isNumberPair() = this.leftChild!!.isNumber() && this.rightChild!!.isNumber()

    fun child(dir: Dir) = if (dir == Dir.Left) this.leftChild!! else this.rightChild!!
    fun setChild(dir: Dir, node: Node) = if (dir == Dir.Left) this.leftChild = node else this.rightChild = node

    fun magnitude(): Int = if (isNumber()) value!! else 3 * leftChild!!.magnitude() + 2 * rightChild!!.magnitude()
}

// unlike explode and split, this does not mutate the tree - it returns a whole new tree
// (perhaps a bit inefficiently, and is tightly coupled to Node.toString, ah well)
// moving this up inside the Node class results in a Kotlin codegen error!!
operator fun Node.plus(node: Node) = grow("[$this,$node]")

// --------------------------------------------------------
// explode and split details (as extension functions so I can have null-safe targets)

// find the leftmost node with 2 value children at depth >= 4
fun Node.findExploding(depth: Int = 0): Node? {
    if (this.isNumber()) return null
    if (this.isNumberPair() && depth >= 4) return this
    return this.leftChild!!.findExploding(depth+1) ?: this.rightChild!!.findExploding(depth+1)
}

// traverse up the tree to find the next [dir] branch
fun Node.findBranch(dir: Dir): Node? {
    if (this.isRoot()) return null
    if (this.side != dir) return this.parent!!.child(dir)
    return this.parent!!.findBranch(dir)
}

// find the next leaf (value node) along the [dir] side of this tree
fun Node?.findLeaf(dir: Dir): Node? {
    if (this == null) return null
    if (this.isNumber()) return this
    return this.child(dir).findLeaf(dir)
}

// take a node with 2 value children, add the left value to the previous node
// in the tree, add the right value to the next node in the tree, replace
// the original node with a value node of 0
fun Node?.explode(): Boolean {
    if (this == null) return false
    findBranch(Dir.Left).findLeaf(Dir.Right)?.incNumber(this.child(Dir.Left).value!!)
    findBranch(Dir.Right).findLeaf(Dir.Left)?.incNumber(this.child(Dir.Right).value!!)
    this.parent!!.setChild(this.side!!, Node(this.parent, this.side, value = 0))
    return true
}

// find the leftmost value node >= 10
fun Node.findSplit(): Node? {
    if (this.isNumber() && this.value!! >= 10) return this
    if (this.isNumber()) return null
    return this.leftChild!!.findSplit() ?: this.rightChild!!.findSplit()
}

// turn a value node into 2 children
fun Node?.split(): Boolean {
    if (this == null) return false
    this.leftChild = Node(this, Dir.Left, value = this.value!! / 2)
    this.rightChild = Node(this, Dir.Right, value = this.value!! - this.leftChild!!.value!!)
    this.value = null
    return true
}

// main snail number reduction entry point
fun Node.snailReduce(): Node {
    while (this.findExploding().explode() || this.findSplit().split()) { /* keep going */ }
    return this
}

// --------------------------------------------------------
// functions dealing with turning a string -> nodes

// turn a string into two strings, one for each child node
// string must be a correctly formed node string:
// '[' + a node string or a single number + ',' +
// node string or a single number + ']'
fun String.extractPairs(): Pair<String, String> {
    // I wish I had found a better way to do this
    // (find the two substrings before and after the outermost comma)
    // I couldn't get a regex that worked
    var i = 0
    var bracketCounter = 0
    val s = this.drop(1).dropLast(1)
    for (ch in s) {
        if (ch == '[') bracketCounter++
        if (ch == ']') bracketCounter--
        if (ch == ',' && bracketCounter == 0) break
        i++
    }
    return Pair(s.substring(0, i), s.substring(i + 1, s.length))
}

// grow a tree from a String
fun grow(s: String, parent: Node? = null, dir: Dir? = null): Node {
    val node = Node(parent, dir, value = s.toIntOrNull())
    // if s is a simple number, this is a leaf node
    if (node.value == null) {
        // otherwise it must have 2 children
        val (left, right) = s.extractPairs()
        node.leftChild = grow(left, node, Dir.Left)
        node.rightChild = grow(right, node, Dir.Right)
    }
    return node
}

// --------------------------------------------------------

val filename = "input-18.txt"
val numbers = java.io.File("../resources/$filename").readLines().map { grow(it) }

// part 1: 4289
val summed = numbers.reduce { acc, e -> (acc + e).snailReduce() }
println(summed.magnitude())

// part 2: 4807

fun <T> allPairs(l: List<T>): Sequence<Pair<T, T>> = sequence {
    for (i in 0 until l.size-1) {
        for (j in i + 1 until l.size) {
            yield(l[i] to l[j])
            yield(l[j] to l[i])
        }
    }
}

println(allPairs(numbers).maxOf { (it.first + it.second).snailReduce().magnitude() })
