package com.exam.galontrans.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ============================================
    // IMPORTANT: THIS BASE_URL IS CONFIGURED FOR YOUR PHP BACKEND
    // ============================================
    // For Android Emulator: Use 10.0.2.2 (points to your PC localhost)
    // For Real Device: Use your PC's IP address (e.g., 192.168.1.100)
    //
    // Your PHP API is in the /restapi/ folder, so we point to that.
    // The endpoint (e.g., api.php?endpoint=products) is handled in ApiService.kt
    // ============================================

    private const val BASE_URL = "http://10.0.2.2/restapi/" // For Emulator
    // private const val BASE_URL = "http://192.168.1.XXX/restapi/" // For Real Device

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
