package com.gotechdynamics.sitarman.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {
    private val BASE_URL = "http://10.0.2.2/laravel_api/public/api"

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("$BASE_URL/login") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            // Mengirim Map menjamin kunci JSON benar-benar sesuai tulisan kita
            setBody(mapOf(
                "email" to request.email,
                "password" to request.password,
                "device_name" to "android"
            ))
        }.body()
    }
}