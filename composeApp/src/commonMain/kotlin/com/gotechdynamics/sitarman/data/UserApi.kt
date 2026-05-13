package com.gotechdynamics.sitarman.data

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApi(private val client: HttpClient, private val settings: Settings) {
    private val BASE_URL = "http://10.0.2.2/laravel_api/public/api/users"

    private fun HttpRequestBuilder.authHeader() {
        val token = settings.getStringOrNull("auth_token")
        if (token != null) {
            header("Authorization", "Bearer $token")
        }
    }

    suspend fun getUsers(search: String? = null, page: Int = 1): UserListResponse {
        val response = client.get(BASE_URL) {
            authHeader()
            parameter("search", search)
            parameter("page", page)
            accept(ContentType.Application.Json)
        }

        if (response.status.value !in 200..299) {
            println("UserApi Error: ${response.status} - ${response.bodyAsText()}")
        }

        return response.body()
    }

    suspend fun createUser(user: User): UserResponse {
        val response = client.post(BASE_URL) {
            authHeader()
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(user)
        }
        return response.body()
    }

    suspend fun updateUser(id: Int, user: User): UserResponse {
        val response = client.put("$BASE_URL/$id") {
            authHeader()
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(user)
        }
        return response.body()
    }

    suspend fun deleteUser(id: Int) {
        client.delete("$BASE_URL/$id") {
            authHeader()
            accept(ContentType.Application.Json)
        }
    }
}