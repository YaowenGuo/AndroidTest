package tech.yaowen.algorithm


fun <T> printMatrixInCircle(matrix: Array<Array<T>>?, optFunc: (T) -> Unit) {
    if (matrix == null || matrix.isEmpty() || matrix[0].isEmpty()) {
        return
    }
    for (i in 0 until (matrix.size - 1)) {
        if (matrix[i].size != matrix[i + 1].size) throw Exception("Input is not a Matrix!")
    }

    var start = 0

    while (start * 2 < matrix.size && start * 2 < matrix[0].size) {
        printMatrixClockWisely(matrix, start, optFunc)
        start++
    }
}

fun <T> printMatrixClockWisely(matrix: Array<Array<T>>, start: Int, optFunc: (T) -> Unit) {
    val endX = matrix[0].size - 1 - start
    val endY = matrix.size - 1 - start
    // 从做到右打印上边
    for (i in start..endX) {
        optFunc(matrix[start][i])
    }

    // 从上到下打印右边
    for (i in (start + 1)..endY) {
        optFunc(matrix[i][endX])
    }

    // 从右到左打印下边
    if (start < endY) {
        for (i in (endX - 1) downTo (start)) {
            optFunc(matrix[endY][i])
        }
    }


    // 从下到上打印左边
    if (start > endX) {
        for (i in (endY - 1) downTo (start + 1)) {
            optFunc(matrix[i][start])
        }
    }
}

fun printInt(value: Int) {
    print(value)
    print(", ")
}

fun main() {
    val matrix1 = Array(4) { row ->
        Array(4) { column -> (row * 4 + column + 1) }
    }

    for (i in matrix1.indices) {
        for (j in matrix1[0].indices) {
            print(matrix1[i][j])
            print(", ")
        }
        println()
    }

    println()
    printMatrixInCircle(matrix1, ::printInt)


    print("\n\nEmpty matrix")
    val matrix2 = Array(0) { Array(0) { 0 } }

    printMatrixInCircle(matrix2, ::printInt)


    print("\n\nOnly one row:")

    val matrix3 = Array(1) { row ->
        Array(4) { column -> (row * 4 + column + 1) }
    }

    println()
    printMatrixInCircle(matrix3, ::printInt)


    print("\n\nOnly one column: ")

    val matrix4 = Array(4) { row ->
        Array(1) { column -> (row * 4 + column + 1) }
    }

    println()
    printMatrixInCircle(matrix4, ::printInt)


}