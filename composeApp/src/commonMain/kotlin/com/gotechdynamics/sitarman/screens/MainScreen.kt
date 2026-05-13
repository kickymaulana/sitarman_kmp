package com.gotechdynamics.sitarman.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gotechdynamics.sitarman.screens.user.ProfileScreen
import com.gotechdynamics.sitarman.screens.user.UserScreen
import com.gotechdynamics.sitarman.screens.user.UserViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAddUser: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val viewModel = koinViewModel<UserViewModel>()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Memuat data pertama kali saat screen muncul
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive && selectedTab == 0) {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.loadUsers(it)
                            },
                            placeholder = { Text("Cari User...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    } else {
                        Text(if (selectedTab == 0) "Daftar User" else "Profil Saya")
                    }
                },
                actions = {
                    if (selectedTab == 0) {
                        if (isSearchActive) {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                                viewModel.loadUsers("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Search")
                            }
                        } else {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Open Search")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Users") },
                    icon = { Icon(Icons.Default.List, contentDescription = "Users") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Profil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = onNavigateToAddUser) {
                    Icon(Icons.Default.Add, contentDescription = "Add User")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> UserScreen(
                    onNavigateToDetail = onNavigateToDetail,
                    searchQuery = searchQuery,
                    onRefresh = { viewModel.loadUsers(searchQuery) }
                )
                1 -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}