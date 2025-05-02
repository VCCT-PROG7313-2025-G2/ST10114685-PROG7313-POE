package com.example.budgettracker

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.budgettracker.data.Expense
import com.example.budgettracker.viewmodel.ExpenseViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    username: String,
    viewModel: ExpenseViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val expenses by viewModel.expenses
    val calendar = Calendar.getInstance()

    // Filter state
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    // Category dropdown
    val categories = listOf("All", "Food", "Transport", "Entertainment", "Utilities", "Other")
    var selectedCategory by remember { mutableStateOf("All") }
    val categoryExpanded = remember { mutableStateOf(false) }

    // Load expenses on init
    LaunchedEffect(Unit) {
        viewModel.getExpensesForUser(username)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Expenses for $username", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        // Date pickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, y, m, d -> startDate = "$y-${m + 1}-$d" },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (startDate.isBlank()) "Start Date" else "From: $startDate")
            }

            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, y, m, d -> endDate = "$y-${m + 1}-$d" },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (endDate.isBlank()) "End Date" else "To: $endDate")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded.value,
            onExpandedChange = { categoryExpanded.value = !categoryExpanded.value }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedCategory,
                onValueChange = {},
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded.value)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded.value,
                onDismissRequest = { categoryExpanded.value = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter / Clear Filter buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    if (startDate.isNotBlank() && endDate.isNotBlank()) {
                        viewModel.getExpensesByFilters(username, startDate, endDate, selectedCategory)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Filter")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    startDate = ""
                    endDate = ""
                    selectedCategory = "All"
                    viewModel.getExpensesForUser(username)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear Filter")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expense list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(expenses) { expense ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Amount: \$${expense.amount}")
                        Text("Description: ${expense.description}")
                        Text("Category: ${expense.category}")
                        Text("Date: ${expense.date}")
                        Text("Time: ${expense.time}")

                        // Show attached image
                        if (!expense.photoUri.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(expense.photoUri),
                                contentDescription = "Receipt",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.deleteExpense(expense) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }
    }
}
