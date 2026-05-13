package com.gotechdynamics.sitarman.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val name: String = "",
    val email: String = "",
    val password: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class UserListResponse(
    val data: List<User> = emptyList(),
    val links: Links? = null,
    val meta: Meta? = null
)

@Serializable
data class UserResponse(
    val data: User? = null
)

@Serializable
data class Links(
    val first: String? = null,
    val last: String? = null,
    val prev: String? = null,
    val next: String? = null
)

@Serializable
data class Meta(
    @SerialName("current_page")
    val currentPage: Int? = null,
    val from: Int? = null,
    @SerialName("last_page")
    val lastPage: Int? = null,
    val links: List<MetaLink>? = null,
    val path: String? = null,
    @SerialName("per_page")
    val perPage: Int? = null,
    val to: Int? = null,
    val total: Int? = null
)

@Serializable
data class MetaLink(
    val url: String? = null,
    val label: String? = null,
    val active: Boolean? = null
)