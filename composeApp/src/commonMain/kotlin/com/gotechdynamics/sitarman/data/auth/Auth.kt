package com.gotechdynamics.sitarman.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    @SerialName("device_name")
    val device_name: String = "android"
)

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
    val message: String? = null
)