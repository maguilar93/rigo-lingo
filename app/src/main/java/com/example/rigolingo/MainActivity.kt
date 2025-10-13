package com.example.rigolingo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.rigolingo.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.animatedLoginFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        try {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            if (navController.currentDestination?.id == R.id.homeFragment) {
                val currentUser = getCurrentUser()
                if (currentUser != null) {
                    addAvatarToActionBar(menu, currentUser)
                }
            }
        } catch (e: Exception) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }
        return true
    }

    private fun getCurrentUser(): com.example.rigolingo.data.model.LoggedInUser? {
        return try {
            val loginDataSource = com.example.rigolingo.data.LoginDataSource()
            val loginRepository = com.example.rigolingo.data.LoginRepository(loginDataSource)
            loginRepository.user
        } catch (e: Exception) {
            null
        }
    }

    private fun addAvatarToActionBar(menu: Menu, user: com.example.rigolingo.data.model.LoggedInUser) {
        try {
            val avatarItem = menu.add(0, 999, 0, getString(R.string.action_profile))
            
            val avatarView = createAvatarView(user.displayName ?: "User")
            avatarItem.setActionView(avatarView)
            avatarItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            
            avatarView?.setOnClickListener {
                showProfileMenu(it)
            }
        } catch (e: Exception) {   
            menuInflater.inflate(R.menu.menu_main, menu)
        }
    }

    private fun createAvatarView(displayName: String): View? {
        return try {
            val avatarSize = (40 * resources.displayMetrics.density).toInt()
            val marginEnd = (12 * resources.displayMetrics.density).toInt()
            
            val cardView = com.google.android.material.card.MaterialCardView(this).apply {
                val params = android.view.ViewGroup.MarginLayoutParams(avatarSize, avatarSize)
                params.marginEnd = marginEnd
                layoutParams = params
                radius = (avatarSize / 2).toFloat()
                cardElevation = 2 * resources.displayMetrics.density
                setCardBackgroundColor(0xFF6200EE.toInt())
                isClickable = true
                isFocusable = true
            }
            
            val textView = android.widget.TextView(this).apply {
                text = getInitials(displayName)
                textSize = 14f
                setTextColor(android.graphics.Color.WHITE)
                gravity = android.view.Gravity.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            
            cardView.addView(textView)
            cardView
        } catch (e: Exception) {
            null
        }
    }

    private fun getInitials(displayName: String): String {
        return displayName.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { displayName.take(1).uppercase() }
    }

    private fun showProfileMenu(anchor: View) {
        try {
            val popupMenu = androidx.appcompat.widget.PopupMenu(this, anchor)
            popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
            
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_profile -> {
                        try {
                            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragment_to_profileFragment)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(this, getString(R.string.error_profile_navigation), android.widget.Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.action_settings -> {
                        android.widget.Toast.makeText(this, getString(R.string.message_settings_coming_soon), android.widget.Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_logout -> {
                        logout()
                        true
                    }
                    else -> false
                }
            }
            
            popupMenu.show()
        } catch (e: Exception) {
            logout()
        }
    }

    private fun logout() {
        val loginDataSource = com.example.rigolingo.data.LoginDataSource()
        val loginRepository = com.example.rigolingo.data.LoginRepository(loginDataSource)
        loginRepository.logout()
        
        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragment_to_animatedLoginFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            999 -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}