package com.example.rigolingo.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import com.example.rigolingo.data.LoginRepository
import com.example.rigolingo.data.Result
import kotlinx.coroutines.launch

import com.example.rigolingo.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val result = loginRepository.login(username, password)

                if (result is Result.Success) {
                    _loginResult.value =
                        LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                } else if (result is Result.Error) {
                    // Use the specific error message from Firebase
                    _loginResult.value = LoginResult(errorMessage = result.exception.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult(errorMessage = "Network error: ${e.message}")
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            try {
                val result = loginRepository.register(email, password, displayName)

                if (result is Result.Success) {
                    _loginResult.value =
                        LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                } else if (result is Result.Error) {
                    _loginResult.value = LoginResult(errorMessage = result.exception.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult(errorMessage = "Network error: ${e.message}")
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}