/*#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        assert_eq!(2 + 2, 4);
    }
}*/

#![cfg(target_os = "android")]
#![allow(non_snake_case)]

use jni::JNIEnv;
use jni::objects::{JObject, JString};
use jni::sys::{jstring};

#[no_mangle]
pub unsafe extern "C"  fn Java_tech_yaowen_androidrust_Hello_stringFromJNI(env: JNIEnv, _: JObject, input: JString) -> jstring {
    let input: String =
            env.get_string(input).expect("Couldn't get java string!").into();


    let output = env.new_string(format!("Hello, {}!", input))
        .expect("Couldn't create java string!");
    output.into_inner()
}

/*
#[no_mangle]
pub unsafe extern fn Java_com_example_logproject_NativeMethodTest_init(env: JNIEnv, jclass: JObject)
                                                                       -> jstring {

    // 将某个setEnable方法设置为true
    let clazz = env.find_class("com/example/logproject/SDK").unwrap();
    env.call_static_method(clazz, "setEnable", "(Z)V", &[JValue::from(true)]);

    let str = "i'm a so by rust!";
    let str = env.new_string(str.to_owned()).unwrap();
    str.into_inner()
}*/

pub extern "C" fn Java_tech_yaowen_androidrust_Hello_callNativeFun(env: JNIEnv, _: JObject) -> jstring {
    let output = env.new_string("Hello ").unwrap();
    output.into_inner()
}

