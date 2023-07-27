import org.junit.jupiter.api.Test
import tech.yaowen.jni.MyClass

class HelloTest {
    @Test
    fun testHello() {
        MyClass().print()
    }
}