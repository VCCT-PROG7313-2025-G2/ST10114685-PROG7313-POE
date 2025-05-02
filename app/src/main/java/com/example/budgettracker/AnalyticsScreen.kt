package com.example.budgettracker

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.budgettracker.viewmodel.ExpenseViewModel

@Composable
fun AnalyticsScreen(
    username: String,
    viewModel: ExpenseViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val expenses by viewModel.expenses

    var monthlyGoal by remember { mutableStateOf("") }
    var minExpense by remember { mutableStateOf("") }

    val totalExpense = expenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Analytics & Goals", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = monthlyGoal,
            onValueChange = { monthlyGoal = it },
            label = { Text("Monthly Budget Goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = minExpense,
            onValueChange = { minExpense = it },
            label = { Text("Minimum Expense Goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                Toast.makeText(context, "Goals saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Goals")
        }

        Divider(thickness = 1.dp)

        Text("Total Spent: $${"%.2f".format(totalExpense)}")

        if (monthlyGoal.toDoubleOrNull() != null) {
            val goal = monthlyGoal.toDouble()
            val remaining = goal - totalExpense
            Text("Remaining from Goal: $${"%.2f".format(remaining)}")
        }
    }
}
