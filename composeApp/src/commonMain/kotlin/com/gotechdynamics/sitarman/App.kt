package com.gotechdynamics.sitarman

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gotechdynamics.sitarman.screens.MainScreen
import com.gotechdynamics.sitarman.screens.login.LoginScreen
import com.gotechdynamics.sitarman.screens.user.AddUserScreen
import com.gotechdynamics.sitarman.screens.user.EditUserScreen
import com.gotechdynamics.sitarman.screens.user.UserDetailScreen
import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject


@Serializable
object UserDestination

@Serializable
data class UserDetailDestination(val userId: Int)

@Serializable
data class EditUserDestination(val userId: Int)

@Serializable
object AddUserDestination

@Serializable
object LoginDestination

@Composable
fun App() {
    val settings: Settings = koinInject()
    val initialToken = settings.getStringOrNull("auth_token")
    val startDest = if (initialToken != null) UserDestination else LoginDestination

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = startDest) {
                composable<LoginDestination> {
                    LoginScreen(onLoginSuccess = {
                        navController.navigate(UserDestination) {
                            popUpTo(LoginDestination) { inclusive = true }
                        }
                    })
                }
                composable<UserDestination> {
                    MainScreen(
                        onNavigateToAddUser = {
                            navController.navigate(AddUserDestination)
                        },
                        onNavigateToDetail = { userId ->
                            navController.navigate(UserDetailDestination(userId))
                        },
                        onLogout = {
                            navController.navigate(LoginDestination) {
                                popUpTo(UserDestination) { inclusive = true }
                            }
                        }
                    )
                }
                composable<UserDetailDestination> { backStackEntry ->
                    val userId = backStackEntry.toRoute<UserDetailDestination>().userId
                    UserDetailScreen(
                        userId = userId,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToEdit = { id ->
                            navController.navigate(EditUserDestination(id))
                        }
                    )
                }
                composable<EditUserDestination> { backStackEntry ->
                    val userId = backStackEntry.toRoute<EditUserDestination>().userId
                    EditUserScreen(
                        userId = userId,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable<AddUserDestination> {
                    AddUserScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}