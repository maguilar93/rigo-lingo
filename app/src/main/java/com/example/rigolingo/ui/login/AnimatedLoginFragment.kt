package com.example.rigolingo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.rigolingo.R
import com.example.rigolingo.data.LoginDataSource
import com.example.rigolingo.data.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnimatedLoginFragment : Fragment() {
    private val loginRepository = LoginRepository(LoginDataSource())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_animated_login, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val animationView: LottieAnimationView = view.findViewById(R.id.lottie_animation_view)

        if (loginRepository.isLoggedIn) {
            findNavController().navigate(
                R.id.action_animatedLoginFragment_to_homeFragment,
            )
            return
        }

        animationView.playAnimation()

        view.setOnClickListener {
            navigateToLogin()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            if (isAdded) {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        try {
            findNavController().navigate(
                R.id.action_animatedLoginFragment_to_loginFragment,
            )
        } catch (e: Exception) {
            // TODO: Handle navigation exception gracefully
        }
    }
}
