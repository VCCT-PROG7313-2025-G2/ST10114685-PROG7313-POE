package com.example.budgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.budgettracker.ui.theme.BudgetTrackerTheme
import com.example.budgettracker.viewmodel.LoginViewModel
import com.example.budgettracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BudgetTrackerTheme {
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val expenseViewModel: ExpenseViewModel = viewModel()

                var loggedInUser by remember { mutableStateOf<String?>(null) }

                NavHost(navController, startDestination = "login") {

                    // Login screen
                    composable("login") {
                        LoginScreen(
                            onLogin = { username, password ->
                                loginViewModel.login(username, password)
                            },
                            onRegisterClick = {
                                navController.navigate("register")
                            }
                        )

                        // Observe login result
                        loginViewModel.loginResult.observe(this@MainActivity) { success ->
                            Toast.makeText(
                                this@MainActivity,
                                if (success) "Login successful!" else "Invalid credentials.",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (success) {
                                loggedInUser = loginViewModel.loggedInUsername
                                loggedInUser?.let {
                                    navController.navigate("dashboard/$it")
                                }
                            }
                        }
                    }

                    // Register screen
                    composable("register") {
                        RegisterScreen(
                            navController = navController,
                            onRegister = { username, password ->
                                loginViewModel.register(username, password)
                            }
                        )
                    }

                    // Observe register result globally
                    loginViewModel.registerResult.observe(this@MainActivity) { success ->
                        Toast.makeText(
                            this@MainActivity,
                            if (success) "Registered successfully!" else "User already exists.",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (success) navController.popBackStack()
                    }

                    // Dashboard screen
                    composable(
                        "dashboard/{username}",
                        arguments = listOf(navArgument("username") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: "User"
                        DashboardScreen(username = username, navController = navController)
                    }

                    // Add expense screen
                    composable(
                        "add_expense/{username}",
                        arguments = listOf(navArgument("username") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: "User"
                        AddExpenseScreen(username = username, viewModel = expenseViewModel)
                    }

                    // View expenses screen
                    composable("view_expenses") {
                        loggedInUser?.let { user ->
                            ExpenseListScreen(
                                username = user,
                                viewModel = expenseViewModel,
                                navController = navController
                            )
                        }
                    }

                    // Analytics screen
                    composable("analytics") {
                        AnalyticsScreen(
                            username = loggedInUser ?: "User",
                            viewModel = expenseViewModel,
                            navController = navController
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            onLogout = {
                                loggedInUser = null
                            }
                        )
                    }


                }
            }
        }
    }
}
