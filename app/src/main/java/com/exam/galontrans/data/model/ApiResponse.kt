package com.exam.galontrans.data.model

import com.google.gson.annotations.SerializedName


data class ApiResponse<T>(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("error")
    val error: String? = null
)