package com.example.test_retrofit.net

import retrofit2.Response
import java.io.IOException

class ApiException constructor(
    private val code: Int,
    message: String,
    private val response: Response<*>? = null): IOException(message) {

    constructor(code: Int, message: String) : this(code, message, null) {

    }


}