package com.gotechdynamics.sitarman.data.user

import com.gotechdynamics.sitarman.data.ApiConstants
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
        val response: HttpResponse = client.post(BASE_URL) {
            authHeader()
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(user)
        }

        if (response.status.value in 200..299) {
            return response.body()
        } else {
            val errorBody = response.bodyAsText()
            println("UserApi Error: ${response.status} - $errorBody")
            val message = parseErrorMessage(errorBody)
            throw Exception(message)
        }
    }

    suspend fun updateUser(id: Int, user: User): UserResponse {
        val response: HttpResponse = client.put("$BASE_URL/$id") {
            authHeader()
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(user)
        }

        if (response.status.value in 200..299) {
            return response.body()
        } else {
            val errorBody = response.bodyAsText()
            println("UserApi Error: ${response.status} - $errorBody")
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
            
            // Laravel biasanya mengirim pesan utama di field 'message'
            val message = json["message"]?.jsonPrimitive?.content
            
            // Dan detail error validasi di field 'errors'
            val errors = json["errors"]?.jsonObject
            
            if (errors != null && errors.isNotEmpty()) {
                // Ambil semua pesan error dari semua field dan gabungkan
                val allErrors = mutableListOf<String>()
                errors.forEach { (_, value) ->
                    value.jsonArray.forEach { error ->
                        allErrors.add(error.jsonPrimitive.content)
                    }
                }
                if (allErrors.isNotEmpty()) {
                    return allErrors.joinToString("\n")
                }
            }
            
            message ?: "Terjadi kesalahan (Status: $jsonString)"
        } catch (e: Exception) {
            "Gagal memproses pesan error dari server"
        }
    }
}