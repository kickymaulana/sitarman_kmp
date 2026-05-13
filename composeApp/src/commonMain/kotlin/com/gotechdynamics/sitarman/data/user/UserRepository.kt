package com.gotechdynamics.sitarman.data.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val userApi: UserApi) {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false

    suspend fun fetchUsers(search: String? = null, isRefresh: Boolean = true) {
        if (isRefresh) {
            currentPage = 1
            isLastPage = false
        }

        if (isLastPage) return

        try {
            val response = userApi.getUsers(search, currentPage)
            val newUsers = response.data ?: emptyList()

            if (isRefresh) {
                _users.value = newUsers
            } else {
                _users.value = _users.value + newUsers
            }

            val lastPage = response.meta?.lastPage ?: 1
            if (currentPage >= lastPage) {
                isLastPage = true
            } else {
                currentPage++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun canLoadMore(): Boolean = !isLastPage

    suspend fun addUser(name: String, email: String, pass: String) {
        try {
            val response = userApi.createUser(User(name = name, email = email, password = pass))
            response.data?.let { newUser ->
                _users.value = listOf(newUser) + _users.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun editUser(id: Int, name: String, email: String) {
        try {
            val response = userApi.updateUser(id, User(name = name, email = email))
            response.data?.let { updatedUser ->
                _users.value = _users.value.map { if (it.id == id) updatedUser else it }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeUser(id: Int) {
        try {
            userApi.deleteUser(id)
            _users.value = _users.value.filter { it.id != id }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}