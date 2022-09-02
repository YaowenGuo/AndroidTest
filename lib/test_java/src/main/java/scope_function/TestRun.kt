package scope_function

//class Dialog {
//    context context;
// fun show(): String {
//     context?.let {
//
//     }
// }
//
//}

fun main() {
    val name = "write code"
    val name2 = run {
        var name = "read code"
        println(name) //输出read code
        name = "write code"
        name
    }
    println(name) //输出write code
    println(name==name2) //输出true
}