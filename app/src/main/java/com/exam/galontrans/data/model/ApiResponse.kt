package com.exam.galontrans.data.model

import com.google.gson.annotations.SerializedName


// Request body for creating transaction
data class CreateTransactionRequest(
    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("quantity")
    val quantity: Int
)
// Request body for updating stock
data class UpdateStockRequest(
    @SerializedName("delta")
    val delta: Int
)
// Generic API Response
data class ApiResponse(
    @SerializedName("message")
    val message: String = "",

    @SerializedName("error")
    val error: String? = null
)