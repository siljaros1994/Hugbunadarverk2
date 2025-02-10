package `is`.hbv601.hugbunadarverk2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ApiClient {
    private const val BASE_URL = "http://192.168.101.4/login-registration/"

    // Create Logger
    val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // Create OkHttpClient
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()


    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // For JSON responses
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}