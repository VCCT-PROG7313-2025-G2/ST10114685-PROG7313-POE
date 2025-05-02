package com.example.budgettracker.data

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun register(user: User): Long
}
