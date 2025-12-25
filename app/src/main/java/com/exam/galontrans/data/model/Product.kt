package com.exam.galontrans.data.model

import com.google.gson.annotations.SerializedName
data class Product(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("price")
    val price: Double = 0.0,

    @SerializedName("image")
    val image: String = "",

    @SerializedName("stock")
    val stock: Int = 0
) {
    // Helper function to get full image URL
    fun getImageUrl(baseUrl: String = "http://10.0.2.2:3000"): String {
        return if (image.isNotEmpty() && image != "[object Object]") {
            "$baseUrl/upload/$image"
        } else {
            ""
        }
    }

    // Check if product is available
    fun isAvailable(): Boolean = stock > 0

    // Get stock status
    fun getStockStatus(): StockStatus {
        return when {
            stock < 5 -> StockStatus.LOW
            stock < 20 -> StockStatus.MEDIUM
            else -> StockStatus.HIGH
        }
    }

    // Get formatted price
    fun getFormattedPrice(): String {
        return "Rp ${String.format("%,.0f", price)}"
    }
}
enum class StockStatus {
    LOW, MEDIUM, HIGH
}