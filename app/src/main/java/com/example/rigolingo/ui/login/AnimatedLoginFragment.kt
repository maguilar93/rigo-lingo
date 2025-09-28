package com.example.rigolingo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.rigolingo.R
import com.example.rigolingo.data.LoginDataSource
import com.example.rigolingo.data.LoginRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnimatedLoginFragment : Fragment() {

    private val loginRepository = LoginRepository(LoginDataSource())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_animated_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animationView: LottieAnimationView = view.findViewById(R.id.lottie_animation_view)
        
        // Check if user is already logged in
        if (loginRepository.isLoggedIn) {
            // User is already logged in, navigate to home
            findNavController().navigate(
                R.id.action_animatedLoginFragment_to_homeFragment
            )
            return
        }
        
        // Start animation and navigate to login form after delay
        animationView.playAnimation()
        
        // Add click listener to skip animation
        view.setOnClickListener {
            navigateToLogin()
        }
        
        // Navigate to login form after animation completes or timeout
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000) // 3 seconds delay
            if (isAdded) {
                navigateToLogin()
            }
        }
    }
    
    private fun navigateToLogin() {
        try {
            findNavController().navigate(
                R.id.action_animatedLoginFragment_to_loginFragment
            )
        } catch (e: Exception) {
            // Handle navigation exception gracefully
        }
    }
}
