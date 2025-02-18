package com.getcard.pinpadpdvexample.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //    private const val BASE_URL = "https://dev-hubpay-getcard.cloudsg.com.br"
    private const val BASE_URL = "http://172.16.100.143:8080"

    val posService: PosService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PosService::class.java)
    }
}