package com.example.budgettracker.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.AppDatabase
import com.example.budgettracker.data.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = AppDatabase.getInstance(application).expenseDao()
    var expenses = mutableStateOf<List<Expense>>(emptyList())

    fun addExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseDao.insert(expense)
            getExpensesForUser(expense.username)
        }
    }

    fun getExpensesForUser(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            expenses.value = expenseDao.getExpenses(username)
        }
    }

    fun getExpensesByDateRange(username: String, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            expenses.value = expenseDao.getExpensesByDateRange(username, startDate, endDate)
        }
    }

    fun getExpensesByFilters(username: String, startDate: String, endDate: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = expenseDao.getExpensesByDateRange(username, startDate, endDate)
            expenses.value = if (category == "All") results else results.filter { it.category == category }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseDao.delete(expense)
            getExpensesForUser(expense.username)
        }
    }
}
