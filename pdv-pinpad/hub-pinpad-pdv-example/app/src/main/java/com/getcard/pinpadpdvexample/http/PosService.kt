package com.getcard.pinpadpdvexample.http

import com.getcard.hubinterface.transaction.TransactionParams
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PosService {
    @POST("api/hub-payment/transaction")
    fun startTransactionOnPos(
        @Header("Authorization") token: String,
        @Body request: TransactionParams
    ): Call<Unit>
}