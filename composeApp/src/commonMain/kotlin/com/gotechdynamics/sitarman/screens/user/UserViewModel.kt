package com.gotechdynamics.sitarman.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechdynamics.sitarman.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val users = userRepository.users

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage = _isLastPage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers(query: String? = null) {
        viewModelScope.launch {
            _isRefreshing.value = true
            userRepository.fetchUsers(query, isRefresh = true)
            _isLastPage.value = !userRepository.canLoadMore()
            _isRefreshing.value = false
        }
    }

    fun loadMore(query: String? = null) {
        if (_isLoadingMore.value || _isLastPage.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            userRepository.fetchUsers(query, isRefresh = false)
            _isLastPage.value = !userRepository.canLoadMore()
            _isLoadingMore.value = false
        }
    }

    fun createUser(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.addUser(name, email, pass)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(id: Int, name: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.editUser(id, name, email)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.removeUser(id)
            } finally {
                _isLoading.value = false
            }
        }
    }
}