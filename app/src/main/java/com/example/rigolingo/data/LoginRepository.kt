package com.example.rigolingo.data

import com.example.rigolingo.data.model.LoggedInUser

class LoginRepository(val dataSource: LoginDataSource) {
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = dataSource.isLoggedIn()

    init {
        user = dataSource.getCurrentUser()
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(
        username: String,
        password: String,
    ): Result<LoggedInUser> {
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
    ): Result<LoggedInUser> {
        val result = dataSource.register(email, password, displayName)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}
