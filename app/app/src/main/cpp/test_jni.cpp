#include <jni.h>
#include <cstdio>
#include <string>
#include "util/debug_util.h"
#include "a.h"

#define TestEntry(func, ...) \
  Java_tech_yaowen_test_jni_TestEntry_##func (__VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
TestEntry(print, JNIEnv *env, jobject jTestEntry) {
//    printf("Hello world");

//    env->CallVoidMethod(jTestEntry, nullptr);
    auto strCar = env->GetStringUTFChars(nullptr, nullptr);
    std::string str("");

    env->ReleaseStringUTFChars(nullptr, strCar);

    if (strCar == nullptr) {
        LOGD("strChar is null");
    }

}


extern "C"
JNIEXPORT void JNICALL
TestEntry(printValue, JNIEnv *env, jobject jTarget, jstring value) {
    auto strCar = env->GetStringUTFChars(value, nullptr);
    if (strCar == nullptr) {
        LOGD("strChar is null");
    }
    std::string str(strCar);
    env->ReleaseStringUTFChars(value, strCar);
    LOGD("strChar value after releae %s", str.c_str());
}


class Shape {
public:
    virtual double area() const = 0;
};

class Rectangle : public Shape {
public:
    Rectangle(double w, double h) : width(w), height(h) {}
    double area() const override {
        return width * height;
    }
private:
    double width, height;
};

class Circle : public Shape {
public:
    Circle(double r) : radius(r) {}


    double area() const override {
        return 3.1415926 * radius * radius;
    }


private:
    double radius;
};

struct Foo{
    virtual int g() = 0;
};

struct Bar: Foo{
    int g(){ return 42; }
};


extern "C" JNIEXPORT jstring JNICALL
TestEntry(stringFromJNI, JNIEnv* env, jobject /* this */) {
    // Use-after-free error, caught by asan and hwasan.
    int* foo = new int;
    *foo = 3;
    delete foo;
    *foo = 4;

    // Signed integer overflow. Undefined behavior caught by ubsan.
//    int k = 0x7fffffff;
//    k += 1;
//    auto array = new int[2];
//    array[0] = array[1] + array[2];
//    A *aPtr = new AChild();
//    aPtr->test();
//    delete aPtr;
//    Shape* shapes[] = {new Rectangle(3, 4), new Circle(2)};
//    for (int i = 0; i < 2; i++) {
//        LOGE("Area: %d", shapes[i]->area());
//        delete shapes[i];
//    }
    auto bar = new Bar();
    bar->g();
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
