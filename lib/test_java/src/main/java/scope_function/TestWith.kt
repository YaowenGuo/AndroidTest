package scope_function

class Person(
    var name: String,
    var age: UShort
) {

    fun convert() {
        name.let {

        }
    }
}

fun main() {
    val person = Person("xiaoxi", 13u)
    person.apply {
        this.age
    }
}