package com.exam.galontrans.data.model

import com.google.gson.annotations.SerializedName

data class Transaction (
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("product_price")
    val productPrice: Int?,

    val date: String = ""
) {
    val totalPrice: Int
        get() = (productPrice ?: 0) * quantity
}
