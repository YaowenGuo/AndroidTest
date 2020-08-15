package tech.yaowen.algorithm

open class LinkNode<V>(var value: V?) {
    var next: LinkNode<V>? = null

    constructor(list: Array<V>): this(null) {
        buildLink(this, list)
    }

    protected fun buildLink(linkHead: LinkNode<V>, list: Array<V>):LinkNode<V> {
        var pLinkNode: LinkNode<V> = linkHead
        for (i in list.indices) {
            pLinkNode.next  = LinkNode(list[i])
            pLinkNode = pLinkNode.next!!
            pLinkNode.value = list[i]
            pLinkNode.next = null
        }
        return  linkHead
    }


    public fun buildLink(list: Array<V>):LinkNode<V> {
        val linkHead: LinkNode<V> = LinkNode<V>(null)
        return buildLink(linkHead, list)
    }

    public fun printLink() {
        var pNode = this
        while (pNode.next != null) {
            pNode = pNode.next!!
            print(pNode.value)
            print(", ")
        }
    }

    fun reverseLink(linkHead:LinkNode<V>) {
        var pLinkNode: LinkNode<V>? = linkHead.next
        var pPreNode: LinkNode<V>
        linkHead.next = null
        while (pLinkNode != null) {
            pPreNode = pLinkNode
            pLinkNode = pLinkNode.next

            pPreNode.next = linkHead.next
            linkHead.next = pPreNode
        }
    }

    fun merge(linkHead: LinkNode<V>, compare:(LinkNode<V>, LinkNode<V>) -> Int) {
        var pNode = this
        var pInsertNode = linkHead
        while (linkHead.next != null) {
            while(pNode.next != null && compare(pNode.next!!, linkHead.next!!) <= 0) {
                pNode = pNode.next!!
            }
            pInsertNode = linkHead.next!!
            linkHead.next = pInsertNode.next
            pInsertNode.next = pNode.next
            pNode.next = pInsertNode
        }
    }

}

fun compareInt(a:LinkNode<Int>, b:LinkNode<Int>): Int {
    return (a.value ?: 0) - (b.value ?: 0)
}


fun main() {
    val linkedList = LinkNode(Array(0) { it * 2 })
    linkedList.printLink()
//    linkedList.reverseLink(linkedList)
//    println("")
//    linkedList.printLink()
//
//    val find = findKthToTail(linkedList, 10u)
//    find?.let {
//        print("\nFind: ")
//        println(it.value)
//    }

    println()
    val linkedList1 = LinkNode(Array(10) { it * 2  + 1 })
    linkedList1.printLink()
    println()
    linkedList.merge(linkedList1, ::compareInt)
    linkedList.printLink()

    println()
    linkedList1.printLink()

}