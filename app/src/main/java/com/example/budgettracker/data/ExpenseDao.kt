package com.example.budgettracker.data

import androidx.room.*

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE username = :username ORDER BY date DESC")
    suspend fun getExpenses(username: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE username = :username AND category = :category ORDER BY date DESC")
    suspend fun getExpensesByCategory(username: String, category: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE username = :username AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesByDateRange(username: String, startDate: String, endDate: String): List<Expense>
}
