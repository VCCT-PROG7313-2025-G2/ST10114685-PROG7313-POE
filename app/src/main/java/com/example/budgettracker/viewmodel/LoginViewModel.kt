package com.example.budgettracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budgettracker.data.AppDatabase
import com.example.budgettracker.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.budgettracker.data.UserDao

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    var loggedInUsername: String? = null
        private set

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.login(username, password)
            _loginResult.postValue(user != null)
            if (user != null) {
                loggedInUsername = username
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = User(username = username, password = password)
            val result = userDao.register(user)
            _registerResult.postValue(result > 0)
        }
    }
}
