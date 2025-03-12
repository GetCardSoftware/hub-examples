package com.getcard.pinpadpdvexample.http

import android.os.Parcelable
import com.getcard.hubinterface.OperationStatus
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TransactionResponseDTO(
    val status: OperationStatus,
    val message: String,
    val transactionTimestamp: String,

    // Only if transaction succeeded
    val nsuHost: String? = null,
    val customerReceipt: String? = null,
    val establishmentReceipt: String? = null,
) : Parcelable
