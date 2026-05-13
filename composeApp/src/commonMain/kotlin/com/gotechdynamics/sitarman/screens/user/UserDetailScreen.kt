package com.gotechdynamics.sitarman.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit
) {
    val viewModel = koinViewModel<UserViewModel>()
    val users by viewModel.users.collectAsState()
    val user = users.find { it.id == userId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail User") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(userId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit User")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus User", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nama", style = MaterialTheme.typography.labelLarge)
                        Text(user.name, style = MaterialTheme.typography.headlineSmall)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Email", style = MaterialTheme.typography.labelLarge)
                        Text(user.email, style = MaterialTheme.typography.bodyLarge)

                        user.createdAt?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Terdaftar Sejak", style = MaterialTheme.typography.labelLarge)
                            Text(it, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("User tidak ditemukan")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus User") },
            text = { Text("Apakah Anda yakin ingin menghapus user ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(userId)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}