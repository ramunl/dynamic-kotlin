package ru.rian.dynamics.retrofit.model

import com.google.gson.annotations.SerializedName

data class TerminalLoginModel(
    @SerializedName("token") var token: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null
)