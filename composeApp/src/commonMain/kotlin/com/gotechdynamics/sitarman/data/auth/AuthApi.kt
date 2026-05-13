package com.gotechdynamics.sitarman.data.auth

import com.gotechdynamics.sitarman.data.ApiConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {
    private val BASE_URL = "${ApiConstants.BASE_URL}/login"

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(mapOf(
                "username" to request.username,
                "password" to request.password,
                "device_name" to "android"
            ))
        }.body()
    }
}