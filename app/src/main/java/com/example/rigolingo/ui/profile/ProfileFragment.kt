package com.example.rigolingo.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import com.example.rigolingo.databinding.FragmentProfileBinding
import com.example.rigolingo.data.LoginRepository
import com.example.rigolingo.data.LoginDataSource
import com.google.android.material.snackbar.Snackbar
import com.example.rigolingo.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val loginRepository = LoginRepository(LoginDataSource())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUserInfo()
        setupClickListeners()
    }
    
    private fun setupUserInfo() {
        val currentUser = loginRepository.user
        if (currentUser != null) {
            binding.profileNameDisplay.text = currentUser.displayName
            binding.profileEmailDisplay.text = getUserEmail()
            
            (activity as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Profile Settings"
        }
    }
    
    private fun getUserEmail(): String {
        return FirebaseAuth.getInstance().currentUser?.email ?: "user@rigolingo.com"
    }
    
    private fun setupClickListeners() {
        binding.editNameButton.setOnClickListener {
            showEditNameDialog()
        }
        
        binding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }
    
    
    private fun showEditNameDialog() {
        val input = android.widget.EditText(requireContext())
        input.setText(binding.profileNameDisplay.text)
        input.hint = getString(R.string.hint_display_name)
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_edit_name_title))
            .setView(input)
            .setPositiveButton(getString(R.string.button_save)) { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    updateDisplayName(newName)
                } else {
                    Snackbar.make(binding.root, getString(R.string.error_name_empty), Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.button_cancel), null)
            .show()
    }
    
    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val currentPasswordInput = dialogView.findViewById<android.widget.EditText>(R.id.current_password_input)
        val newPasswordInput = dialogView.findViewById<android.widget.EditText>(R.id.new_password_input)
        val confirmPasswordInput = dialogView.findViewById<android.widget.EditText>(R.id.confirm_password_input)
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_change_password_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.button_change_password)) { _, _ ->
                val currentPassword = currentPasswordInput.text.toString()
                val newPassword = newPasswordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()
                
                if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
                    changePassword(currentPassword, newPassword)
                }
            }
            .setNegativeButton(getString(R.string.button_cancel), null)
            .show()
    }
    
    
    private fun updateDisplayName(newName: String) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.profileNameDisplay.text = newName
                        Snackbar.make(binding.root, getString(R.string.success_name_updated), Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(binding.root, getString(R.string.error_name_update_failed), Snackbar.LENGTH_SHORT).show()
                    }
                }
        } else {
            Snackbar.make(binding.root, getString(R.string.error_user_not_logged_in), Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun validatePasswordChange(current: String, new: String, confirm: String): Boolean {
        return when {
            current.isBlank() -> {
                Snackbar.make(binding.root, getString(R.string.error_current_password_empty), Snackbar.LENGTH_SHORT).show()
                false
            }
            new.length < 6 -> {
                Snackbar.make(binding.root, getString(R.string.error_password_too_short), Snackbar.LENGTH_SHORT).show()
                false
            }
            new != confirm -> {
                Snackbar.make(binding.root, getString(R.string.error_passwords_dont_match), Snackbar.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
    
    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null && user.email != null) {
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Snackbar.make(binding.root, getString(R.string.success_password_changed), Snackbar.LENGTH_SHORT).show()
                                } else {
                                    Snackbar.make(binding.root, getString(R.string.error_password_change_failed), Snackbar.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Snackbar.make(binding.root, getString(R.string.error_current_password_incorrect), Snackbar.LENGTH_SHORT).show()
                    }
                }
        } else {
            Snackbar.make(binding.root, getString(R.string.error_user_not_authenticated), Snackbar.LENGTH_SHORT).show()
        }
    }
    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
