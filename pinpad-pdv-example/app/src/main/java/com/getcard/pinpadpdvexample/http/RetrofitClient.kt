package com.getcard.pinpadpdvexample.http

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun <T> handleApiError(response: Response<T>): ApiError? {
    return try {
        val errorBody = response.errorBody()?.string()
        Gson().fromJson(errorBody, ApiError::class.java)
    } catch (e: Exception) {
        Log.e("HandleApiError", "Error: ${e.message}")
        null
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://dev-hubpay.tefbr.com.br"
//    private const val BASE_URL = "http://172.16.100.141:8080"


    val posService: PosService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PosService::class.java)
    }
}