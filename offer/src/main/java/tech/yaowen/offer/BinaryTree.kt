package tech.yaowen.offer

import java.lang.Exception
import java.util.*


open class BinaryTreeNode<T>(
    var value: T? = null,
    var left: BinaryTreeNode<T>? = null,
    var right: BinaryTreeNode<T>? = null
) {

    companion object {
        /**
         * listData a list of data give by complete binary tree(完全二叉树).
         */
        private var index = 0

        @JvmStatic
        private fun <T> buildTree(listData: Array<T?>): BinaryTreeNode<T>? {
            index = 0
            return build(listData)
        }


        @JvmStatic
        private fun <T> build(listData: Array<T?>): BinaryTreeNode<T>? {
            return if (index < listData.size) {
                val data = listData[index++]
                if (data != null) {
                    val node = BinaryTreeNode(data, null, null)
                    node.left = build(listData)
                    node.right = build(listData)
                    node
                } else {
                    null
                }
            } else {
                null
            }
        }

        @JvmStatic
        private fun <T> buildByCycle(listData: Array<T?>): BinaryTreeNode<T>? {
            var i = 0
            val stack = Stack<BinaryTreeNode<T>>()
            var last: BinaryTreeNode<T>? = null
            while (i < listData.size) {
                val data = listData[i++]
                if (data != null) {
                    val node = BinaryTreeNode(data, null, null)
                    if (last != null) {
                        last.left = node
                        last = node
                        stack.push(node)
                    } else if (!stack.isEmpty()) {
                        last = stack.pop()
                        last.right = node
                    }

                } else {
                    last = null
                }
            }

            return last
        }
    }

    constructor(preOrder: Array<T>, inOrder: Array<T>) : this(null, null, null) {
        if (preOrder.isEmpty() || inOrder.isEmpty()) {
            throw Exception("Input is Empty!")
        } else if (preOrder.size != inOrder.size) {
            throw Exception("Invalid input.")
        }

        constructCore(
            preOrder, 0, preOrder.size - 1,
            inOrder, 0, inOrder.size - 1
        )
    }


    protected fun constructCore(
        preOrder: Array<T>, startPre: Int, endPre: Int
        , inOrder: Array<T>, startIn: Int, endIn: Int
    ): BinaryTreeNode<T> {
        if (startPre > endPre || startIn > endIn || (endPre - startPre) != (endIn - startIn)) {
            throw Exception("Input length less 1 or per-order traversal length not equal in-order traversal.")
        }
        var nodeAtInOrderIndex = -1
        for (i in startIn..endIn) {
            if (preOrder[startPre] == inOrder[i]) {
                nodeAtInOrderIndex = i
                break
            }
        }
        if (nodeAtInOrderIndex == -1) {
            throw Exception("Can't find same node at pre-order and in-order traversal.")
        }

        value = preOrder[startPre]


        val inOrderLeftLength = nodeAtInOrderIndex - startIn
        val inOrderRightLength = endIn - nodeAtInOrderIndex
        if (inOrderLeftLength > 0) {
            left = BinaryTreeNode<T>(null, null, null)
                .constructCore(
                    preOrder, startPre + 1, startPre + inOrderLeftLength,
                    inOrder, startIn, nodeAtInOrderIndex - 1
                )
        }
        if (inOrderRightLength > 0) {
            right = BinaryTreeNode<T>(null, null, null)
                .constructCore(
                    preOrder, endPre - inOrderRightLength + 1, endPre,
                    inOrder, nodeAtInOrderIndex + 1, endIn
                )
        }

        return this
    }


    fun hasSubtree(node: BinaryTreeNode<T>?, equal: (T?, T?) -> Boolean): Boolean {
        var has = false
        node?.let {
            if (equal(this.value, it.value)) {
                has = containAnotherTree(this, node, equal)
            }
            if (!has) {
                has = left?.hasSubtree(node, equal) ?: false
            }
            if (!has) {
                has = right?.hasSubtree(node, equal) ?: false
            }
        }
        return has
    }

    private fun containAnotherTree(
        node1: BinaryTreeNode<T>?,
        node2: BinaryTreeNode<T>?,
        equal: (T?, T?) -> Boolean
    ): Boolean {
        if (node2 == null) {
            return true
        }
        if (node1 == null) {
            return false
        }

        return equal(node1.value, node1.value)
                && containAnotherTree(node1.left, node2.left, equal)
                && containAnotherTree(node1.right, node2.right, equal)
    }

    public fun preOrderTraversal(nodeOpt: (BinaryTreeNode<T>) -> Unit) {
        value?.let { nodeOpt(this) }
        left?.let { it.preOrderTraversal(nodeOpt) }
        right?.let { it.preOrderTraversal(nodeOpt) }
    }

    public fun preOrderTraversal1(nodeOpt: (BinaryTreeNode<T>) -> Unit) {
        val stack = Stack<BinaryTreeNode<T>>()
        stack.push(this)
        while (!stack.empty()) {
            val node = stack.pop()
            nodeOpt(node)
            if (node.right != null) {
                stack.push(right)
            }
            if (node.left != null) {
                stack.push(left)
            }
        }
    }

    public fun inOrderTraversal(nodeOpt: (BinaryTreeNode<T>) -> Unit) {
        left?.let { it.inOrderTraversal(nodeOpt) }
        value?.let { nodeOpt(this) }
        right?.let { it.inOrderTraversal(nodeOpt) }
    }

    public fun inOrderTraversal1(nodeOpt: (BinaryTreeNode<T>) -> Unit) {
        var node: BinaryTreeNode<T>? = this
        val stack = Stack<BinaryTreeNode<T>>()
        while (node != null || !stack.empty()) {
            if (node != null) {
                stack.push(node)
                node = node.left
            } else {
                node = stack.pop()
                nodeOpt(node!!)
                node = node.right
            }
        }
    }


    public fun postOrderTraversal(nodeOpt: (BinaryTreeNode<T>) -> Unit) {
        left?.let { it.postOrderTraversal(nodeOpt) }
        right?.let { it.postOrderTraversal(nodeOpt) }
        value?.let { nodeOpt(this) }
    }

    public fun postOrderTraversal1(nodeOpt: (BinaryTreeNode<T>) -> Unit) {
        val stack1 = Stack<BinaryTreeNode<T>>()
        val stack2 = Stack<BinaryTreeNode<T>>()
        stack1.push(this)
        while (stack1.empty()) {
            val node = stack1.pop()
            stack2.push(node)
            if (node.left != null) {
                stack1.push(node.left)
            }
            if (node.right != null) {
                stack1.push(node.right)
            }
        }
        while (!stack2.empty()) {
            nodeOpt(stack2.pop())
        }
    }


    public fun searchTreeToSortedLink(firstNode: BinaryTreeNode<T>?): BinaryTreeNode<T> {
        val subFirstNode = right?.searchTreeToSortedLink(firstNode) ?: firstNode

        right = subFirstNode
        if (subFirstNode != null) {
            subFirstNode.left = this
        }
//        print("$value, ")
        return left?.searchTreeToSortedLink(this) ?: this
    }

}

fun findBTreePath(root: BinaryTreeNode<Int>, expectedSum: Int) {
    val stack = Stack<Int>()
    val currentSum = 0
    findBTreePath(root, expectedSum, currentSum, stack)
}

fun findBTreePath(node: BinaryTreeNode<Int>, expectedSum: Int, currentSum: Int, path: Stack<Int>) {
    var sum = currentSum + (node.value ?: 0)
    path.push(node.value ?: 0)

    val isLeaf = node.left == null && node.right == null

    if (currentSum == expectedSum && isLeaf) {
        println("A path is found:")
        for (item in path) {
            print(item); print(", ")
        }
        println()
    }

    if (node.left != null) {
        findBTreePath(node.left!!, expectedSum, sum, path)
    }
    if (node.right != null) {
        findBTreePath(node.right!!, expectedSum, sum, path)
    }

    // 回退到父节点时，删除路径节点
    path.pop()
}

fun printNodeValue(node: BinaryTreeNode<Int>) {
    print(node.value)
    print(", ")
}


/*
fun main() {
    val preOrderTree = intArrayOf(1, 2, 4, 7, 3, 5, 6, 8)
    val inOrderTree = intArrayOf(4, 7, 2, 1, 5, 3, 8, 6)

    val root = BinaryTreeNode(preOrderTree.toTypedArray(), inOrderTree.toTypedArray())

    root.preOrderTraversal {
        val temp = it.left
        it.left = it.right
        it.right = temp
    }

    root.preOrderTraversal(::printNodeValue)

    println()
    root.inOrderTraversal(::printNodeValue)

    println()
    root.postOrderTraversal(::printNodeValue)

}*/


fun main() {
    val preOrderTree = intArrayOf(10, 6, 4, 8, 14, 12, 16)
    val inOrderTree = intArrayOf(4, 6, 8, 10, 12, 14, 16)

    val root = BinaryTreeNode(preOrderTree.toTypedArray(), inOrderTree.toTypedArray())

    root.preOrderTraversal(::printNodeValue)


    println()
    val link = root.searchTreeToSortedLink(null)
    var nodeP: BinaryTreeNode<Int>? = link
//    println(nodeP)
    println()
    while (nodeP != null) {
        print("${nodeP.value}, ")
        nodeP = nodeP.right
    }
    println()

}