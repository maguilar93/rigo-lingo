package com.example.rigolingo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rigolingo.R
import com.example.rigolingo.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading
        val signupText = binding.signupText

        loginViewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            },
        )

        loginViewModel.loginResult.observe(
            viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.errorMessage?.let {
                    showLoginFailedWithMessage(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            },
        )

        usernameEditText.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
            )
        }

        passwordEditText.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
            )
        }
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
            )
        }

        signupText.setOnClickListener {
            showRegistrationDialog()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_SHORT).show()

        // Navigate to home screen
        findNavController().navigate(
            R.id.action_loginFragment_to_homeFragment,
        )
    }

    private fun showLoginFailed(
        @StringRes errorString: Int,
    ) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailedWithMessage(errorMessage: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showRegistrationDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_register, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.email_edit_text)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.password_edit_text)
        val displayNameEditText = dialogView.findViewById<EditText>(R.id.display_name_edit_text)

        AlertDialog.Builder(requireContext())
            .setTitle("Create Account")
            .setView(dialogView)
            .setPositiveButton("Sign Up") { _, _ ->
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val displayName = displayNameEditText.text.toString()

                when {
                    email.isBlank() -> Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        email,
                    ).matches() -> Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show()
                    password.isBlank() -> Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                    password.length < 6 -> Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    displayName.isBlank() -> Toast.makeText(context, "Please enter display name", Toast.LENGTH_SHORT).show()
                    else -> {
                        binding.loading.visibility = View.VISIBLE
                        loginViewModel.register(email, password, displayName)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
