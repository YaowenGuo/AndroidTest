package tech.yaowen.algorithm

class ComplexListNode<T> constructor(
    var value: T?,
    var next: ComplexListNode<T>?,
    var sibling: ComplexListNode<T>?
){
    fun clone(): ComplexListNode<T>? {
        val head = ComplexListNode(this.value, this.next, null)
        cloneAndInsertNode()
        this.each { print("${it.value}, ") }
        println()
        connectSiblingNode()
        return reconnectNode()
    }


    private fun cloneAndInsertNode() {
        var pointer = this
        while (pointer.next != null) {
            val node = ComplexListNode(pointer.next?.value, pointer.next?.next, null)
            pointer.next!!.next = node
            pointer = node
        }
    }

    private fun connectSiblingNode() {
        var pointer = this
        while (pointer.next != null && pointer.next!!.next != null) {
            pointer = pointer.next!!
            pointer.sibling = pointer.next!!.sibling
            pointer = pointer.next!!
        }
    }

    private fun reconnectNode(): ComplexListNode<T>? {
        if (this.next == null) return null
        val head = ComplexListNode(value, next, sibling)
        var oldP = this
        var cloneP = head
        while (oldP.next != null && oldP.next!!.next != null) {
            oldP = oldP.next!!
            cloneP.next = oldP.next!!
            cloneP = cloneP.next!!
            oldP.next = oldP.next!!.next
        }
        return head
    }

    public fun each(opt:(ComplexListNode<T>) -> Unit) {
        var nodeP = this
        while (nodeP.next != null) {
            opt(nodeP.next!!)
            nodeP = nodeP.next!!
        }
    }

}



fun main() {
    val link = ComplexListNode<Int>(null, null, null)
    var nodeP = link
    for (i in 0 .. 9) {
        nodeP.next = ComplexListNode(i, null, null)
        nodeP.sibling = nodeP.next
        nodeP = nodeP.next!!
    }
    nodeP = link
    while (nodeP.next != null) {
        nodeP = nodeP.next!!
        print("${nodeP.value}, ")
    }
    println()

    val copyLink = link.clone()

    println("Link data after clone:")
    nodeP = link
    while (nodeP.next != null) {
        nodeP = nodeP.next!!
        print("${nodeP.value}, ")
    }
    println()

    copyLink?.let {
        println("Clone link data:")
        nodeP = copyLink
        while (nodeP.next != null) {
            nodeP = nodeP.next!!
            print("${nodeP.value}, ")
        }
        println()

        println("Clone link data by sibling:")
        nodeP = copyLink
        while (nodeP.sibling != null) {
            nodeP = nodeP.sibling!!
            print("${nodeP.value}, ")
        }
        println()
    }
}