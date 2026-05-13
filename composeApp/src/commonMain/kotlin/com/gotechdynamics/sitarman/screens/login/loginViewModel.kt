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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginResult.value = null
            try {
                // Pastikan ketiga parameter terisi: email, password, dan device_name
                val response = authApi.login(
                    LoginRequest(
                        email = email.trim(),
                        password = password.trim(),
                        device_name = "android" // Kita paksa kirim string "android"
                    )
                )

                if (response.accessToken != null) {
                    // SIMPAN TOKEN KE MEMORI PERMANEN
                    settings.putString("auth_token", response.accessToken)
                    _loginResult.value = "Berhasil! Token didapat."
                } else if (response.message != null) {
                    _loginResult.value = "Gagal: ${response.message}"
                } else {
                    _loginResult.value = "Gagal: Cek kembali email & password"
                }
            } catch (e: Exception) {
                // Menampilkan error lebih detail (seperti 422, 500, dll)
                _loginResult.value = "Koneksi Gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}