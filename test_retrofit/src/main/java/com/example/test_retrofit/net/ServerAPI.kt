package tools777.ighashtags.model.net

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ServerAPI {

    @GET("users/{user}/repos")
    fun groupList(@Path("user") user: String): Call<ResponseBody>
}