package com.exam.galontrans.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("product_id")
    val productId: Int = 0,

    @SerializedName("quantity")
    val quantity: Int = 0,

    @SerializedName("product_name")
    val productName: String = "",

    @SerializedName("product_price")
    val productPrice: Double = 0.0,

    @SerializedName("product_image")
    val productImage: String = ""
) {
    // Calculate total price
    fun getTotalPrice(): Double = quantity * productPrice

    // Get formatted total
    fun getFormattedTotal(): String {
        return "Rp ${String.format("%,.0f", getTotalPrice())}"
    }

    // Get formatted price
    fun getFormattedPrice(): String {
        return "Rp ${String.format("%,.0f", productPrice)}"
    }

    // Get image URL
    fun getImageUrl(baseUrl: String = "http://10.0.2.2:3000"): String {
        return if (productImage.isNotEmpty() && productImage != "[object Object]") {
            "$baseUrl/upload/$productImage"
        } else {
            ""
        }
    }
}