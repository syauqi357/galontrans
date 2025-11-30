package com.exam.galontrans.data.model

import com.google.gson.annotations.SerializedName

data class Product (
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Int
)
