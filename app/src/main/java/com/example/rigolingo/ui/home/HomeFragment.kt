package com.example.rigolingo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import com.example.rigolingo.databinding.FragmentHomeBinding
import com.example.rigolingo.data.LoginRepository
import com.example.rigolingo.data.LoginDataSource
import com.google.android.material.snackbar.Snackbar
import com.example.rigolingo.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val loginRepository = LoginRepository(LoginDataSource())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        setupMockData()
    }
    
    override fun onResume() {
        super.onResume()
        setupUserInfo()
    }
    
    private fun setupUserInfo() {
        val currentUser = loginRepository.user
        if (currentUser != null) {
            binding.welcomeText.text = "Ready to continue learning?"
            // Set the action bar title after navigation is complete
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Welcome, ${currentUser.displayName}!"
            
            // Setup profile section
            setupProfileInfo(currentUser.displayName, getUserEmail())
        } else {
            binding.welcomeText.text = "Ready to continue learning?"
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Welcome back!"
            setupProfileInfo("User", "user@example.com")
        }
    }
    
    private fun setupProfileInfo(displayName: String, email: String) {
        // Set profile information
        binding.profileDisplayName.text = displayName
        binding.profileEmail.text = email
        binding.profileJoinDate.text = "Joined Sept 2025" // In a real app, this would be dynamic
    }
    
    private fun getInitials(displayName: String): String {
        return displayName.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { displayName.take(1).uppercase() }
    }
    
    private fun getUserEmail(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "user@rigolingo.com"
    }
    
    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            logout()
        }
        
        binding.basicsCard.setOnClickListener {
            Snackbar.make(it, "Basics lessons coming soon! 📚", Snackbar.LENGTH_SHORT).show()
        }
        
        binding.phrasesCard.setOnClickListener {
            Snackbar.make(it, "Phrases lessons coming soon! 💬", Snackbar.LENGTH_SHORT).show()
        }
        
        binding.profileSectionCard.setOnClickListener {
            Snackbar.make(it, "View full profile coming soon! 📝", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun setupMockData() {
        // Mock data for demonstration - in a real app this would come from a database
        binding.streakCount.text = "5"
        binding.totalXp.text = "247"
        binding.dailyProgress.text = "2 / 3 lessons"
        binding.progressBar.progress = 67
    }
    
    private fun logout() {
        loginRepository.logout()
        // Navigate back to animated login
        findNavController().navigate(
            R.id.action_homeFragment_to_animatedLoginFragment
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
