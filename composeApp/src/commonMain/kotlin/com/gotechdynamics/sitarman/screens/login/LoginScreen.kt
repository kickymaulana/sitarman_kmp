package com.gotechdynamics.sitarman.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val viewModel = koinViewModel<LoginViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sitarman 2", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Login")
            }
        }

        loginResult?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = if (it.contains("Berhasil")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

            if (it.contains("Berhasil")) {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
            }
        }
    }
}