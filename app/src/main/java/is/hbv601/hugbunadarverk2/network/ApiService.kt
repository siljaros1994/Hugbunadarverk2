package `is`.hbv601.hugbunadarverk2.network

import `is`.hbv601.hugbunadarverk2.model.LoginRequest
import `is`.hbv601.hugbunadarverk2.model.LoginResponse
import `is`.hbv601.hugbunadarverk2.model.RegisterRequest
import `is`.hbv601.hugbunadarverk2.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("login.php")
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register.php")
    @FormUrlEncoded
    fun registerUser(
        @Field("fullname") fullname: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("usertype") usertype: String
    ): Call<RegisterResponse>
}