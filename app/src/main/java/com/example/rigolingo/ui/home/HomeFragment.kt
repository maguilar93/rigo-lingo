package com.example.rigolingo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rigolingo.R
import com.example.rigolingo.data.LoginDataSource
import com.example.rigolingo.data.LoginRepository
import com.example.rigolingo.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val loginRepository = LoginRepository(LoginDataSource())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
            binding.welcomeText.text = getString(R.string.home_welcome_message)
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.home_welcome_title, currentUser.displayName)

            setupProfileInfo(currentUser.displayName, getUserEmail())
        } else {
            binding.welcomeText.text = getString(R.string.home_welcome_message)
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.home_welcome_title_default)
            setupProfileInfo("User", "user@example.com")
        }
    }

    private fun setupProfileInfo(
        displayName: String,
        email: String,
    ) {
        binding.profileDisplayName.text = displayName
        binding.profileEmail.text = email
        binding.profileLevel.text = getString(R.string.home_level_format, 5)
        binding.profileJoinDate.text = getString(R.string.home_join_date_format, "Sept 2025")
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
            Snackbar.make(it, getString(R.string.home_category_basics_coming_soon), Snackbar.LENGTH_SHORT).show()
        }

        binding.phrasesCard.setOnClickListener {
            Snackbar.make(it, getString(R.string.home_category_phrases_coming_soon), Snackbar.LENGTH_SHORT).show()
        }

        binding.profileSectionCard.setOnClickListener {
            Snackbar.make(it, getString(R.string.home_profile_coming_soon), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupMockData() {
        binding.streakCount.text = "5"
        binding.totalXp.text = "247"
        binding.dailyProgress.text = getString(R.string.home_daily_progress_format, 2, 3)
        binding.progressBar.progress = 67
    }

    private fun logout() {
        loginRepository.logout()
        findNavController().navigate(
            R.id.action_homeFragment_to_animatedLoginFragment,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
