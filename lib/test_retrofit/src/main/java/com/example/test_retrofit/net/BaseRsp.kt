package com.example.test_retrofit.net

class BaseRsp<D> {
    var code = 1
    var msg: String? = null
    var data: D? = null
}