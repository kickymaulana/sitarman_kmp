package com.gotechdynamics.sitarman.data.user

import com.gotechdynamics.sitarman.data.ApiConstants
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class UserApi(private val client: HttpClient, private val settings: Settings) {
    private val BASE_URL = "${ApiConstants.BASE_URL}/users"

    private fun HttpRequestBuilder.authHeader() {
        val token = settings.getStringOrNull("auth_token")
        if (token != null) {
            header("Authorization", "Bearer $token")
        }
    }

    suspend fun getUsers(search: String? = null, page: Int = 1): UserListResponse {
        return client.get(BASE_URL) {
            authHeader()
            parameter("search", search)
            parameter("page", page)
            accept(ContentType.Application.Json)
        }.body()
    }

    suspend fun createUser(user: User): UserResponse {
        try {
            val response: HttpResponse = client.post(BASE_URL) {
                authHeader()
                url {
                    parameters.append("expectSuccess", "true")
                }
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(user)
            }
            return response.body()
        } catch (e: ClientRequestException) {
            val errorBody = e.response.bodyAsText()
            val message = parseErrorMessage(errorBody)
            throw Exception(message)
        }
    }

    suspend fun updateUser(id: Int, user: User): UserResponse {
        try {
            val response: HttpResponse = client.put("$BASE_URL/$id") {
                authHeader()
                url {
                    parameters.append("expectSuccess", "true")
                }
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(user)
            }
            return response.body()
        } catch (e: ClientRequestException) {
            val errorBody = e.response.bodyAsText()
            val message = parseErrorMessage(errorBody)
            throw Exception(message)
        }
    }

    suspend fun deleteUser(id: Int) {
        client.delete("$BASE_URL/$id") {
            authHeader()
            accept(ContentType.Application.Json)
        }
    }

    private fun parseErrorMessage(jsonString: String): String {
        return try {
            val json = Json.parseToJsonElement(jsonString).jsonObject
            val message = json["message"]?.jsonPrimitive?.content ?: "Terjadi kesalahan"
            val errors = json["errors"]?.jsonObject
            if (errors != null) {
                val firstErrorField = errors.keys.firstOrNull()
                if (firstErrorField != null) {
                    val firstErrorMessage = errors[firstErrorField]?.jsonArray?.get(0)?.jsonPrimitive?.content
                    return firstErrorMessage ?: message
                }
            }
            message
        } catch (e: Exception) {
            "Pesan: $jsonString"
        }
    }
}