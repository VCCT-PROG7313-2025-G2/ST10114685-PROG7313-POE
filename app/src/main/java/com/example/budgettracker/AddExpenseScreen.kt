package com.example.budgettracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.budgettracker.data.Expense
import com.example.budgettracker.viewmodel.ExpenseViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(username: String, viewModel: ExpenseViewModel) {
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val categories = listOf("Food", "Transport", "Entertainment", "Utilities", "Other...")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var customCategory by remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }

    val showCustomCategoryField = selectedCategory == "Other..."

    val calendar = Calendar.getInstance()
    var date by remember {
        mutableStateOf("${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}")
    }

    var time by remember {
        mutableStateOf("${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
    }

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            OutlinedTextField(
                value = if (showCustomCategoryField) customCategory else selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded.value = false
                        }
                    )
                }
            }
        }

        if (showCustomCategoryField) {
            OutlinedTextField(
                value = customCategory,
                onValueChange = { customCategory = it },
                label = { Text("Custom Category") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(onClick = {
            DatePickerDialog(context, { _, y, m, d -> date = "$y-${m + 1}-$d" },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Select Date: $date")
        }

        Button(onClick = {
            TimePickerDialog(context, { _, h, m -> time = "$h:$m" },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            ).show()
        }) {
            Text("Select Time: $time")
        }

        Button(onClick = {
            photoPickerLauncher.launch("image/*")
        }) {
            Text("Attach Photo")
        }

        photoUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        Button(
            onClick = {
                val category = if (showCustomCategoryField) customCategory else selectedCategory
                if (amount.isBlank() || category.isBlank()) {
                    Toast.makeText(context, "Fill all required fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                viewModel.addExpense(
                    Expense(
                        username = username,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        description = description,
                        category = category,
                        date = date,
                        time = time,
                        photoUri = photoUri?.toString()
                    )
                )
                Toast.makeText(context, "Expense saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
