package com.gotechdynamics.sitarman.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechdynamics.sitarman.data.user.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserEvent {
    object Success : UserEvent()
    data class Error(val message: String) : UserEvent()
}

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

    private val _events = MutableSharedFlow<UserEvent>()
    val events = _events.asSharedFlow()

    fun loadUsers(query: String? = null) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                userRepository.fetchUsers(query, isRefresh = true)
                _isLastPage.value = !userRepository.canLoadMore()
            } catch (e: Exception) {
                _events.emit(UserEvent.Error(e.message ?: "Gagal memuat data"))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadMore(query: String? = null) {
        if (_isLoadingMore.value || _isLastPage.value || _isRefreshing.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                userRepository.fetchUsers(query, isRefresh = false)
                _isLastPage.value = !userRepository.canLoadMore()
            } catch (e: Exception) {
                _events.emit(UserEvent.Error(e.message ?: "Gagal memuat data lebih banyak"))
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun createUser(name: String, username: String, whatsapp: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.addUser(name, username, whatsapp, email, pass)
                _events.emit(UserEvent.Success)
            } catch (e: Exception) {
                _events.emit(UserEvent.Error(e.message ?: "Gagal menambah user"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(id: Int, name: String, username: String, whatsapp: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.editUser(id, name, username, whatsapp, email)
                _events.emit(UserEvent.Success)
            } catch (e: Exception) {
                _events.emit(UserEvent.Error(e.message ?: "Gagal memperbarui user"))
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
                _events.emit(UserEvent.Success)
            } catch (e: Exception) {
                _events.emit(UserEvent.Error(e.message ?: "Gagal menghapus user"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}