package tech.yaowen.algorithm

@ExperimentalUnsignedTypes
fun <T> findKthToTail(linkHead: LinkNode<T>?, k: UInt):LinkNode<T>? {
    if (linkHead == null || k <= 0u) return null

    var pAhead:LinkNode<T> = linkHead
    var pBehind:LinkNode<T>?
    for (i in 0 until (k.toInt() - 1)) {
        if (pAhead.next != null) {
            pAhead = pAhead.next!!
        } else {
            return null
        }
    }
    pBehind = linkHead
    while (pAhead.next != null) {
        pAhead = pAhead.next!!
        pBehind = pBehind?.next!!
    }
    return pBehind
}