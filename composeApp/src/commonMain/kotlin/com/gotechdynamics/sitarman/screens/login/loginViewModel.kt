package com.gotechdynamics.sitarman.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechdynamics.sitarman.data.auth.AuthApi
import com.gotechdynamics.sitarman.data.auth.LoginRequest
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authApi: AuthApi,
    private val settings: Settings
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginResult = MutableStateFlow<String?>(null)
    val loginResult = _loginResult.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginResult.value = null
            try {
                val response = authApi.login(
                    LoginRequest(
                        username = username.trim(),
                        password = password.trim(),
                        device_name = "android"
                    )
                )

                if (response.accessToken != null) {
                    settings.putString("auth_token", response.accessToken)
                    _loginResult.value = "Berhasil! Token didapat."
                } else if (response.message != null) {
                    _loginResult.value = "Gagal: ${response.message}"
                } else {
                    _loginResult.value = "Gagal: Cek kembali username & password"
                }
            } catch (e: Exception) {
                _loginResult.value = "Koneksi Gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}