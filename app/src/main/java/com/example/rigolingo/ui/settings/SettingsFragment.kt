package com.example.rigolingo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.example.rigolingo.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.example.rigolingo.R
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupLanguageSpinner()
        
        (activity as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Settings"
    }
    
    private fun setupLanguageSpinner() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_french), 
            getString(R.string.language_spanish)
        )
        
        val languageCodes = arrayOf("en", "fr", "es")
        val currentLanguage = getCurrentLanguage()
        
        val currentIndex = languageCodes.indexOf(currentLanguage)
        if (currentIndex != -1) {
            binding.languageSelection.text = languages[currentIndex]
        }
        
        binding.languageCard.setOnClickListener {
            showLanguageSelectionDialog(languages, languageCodes)
        }
    }
    
    private fun showLanguageSelectionDialog(languages: Array<String>, languageCodes: Array<String>) {
        val currentLanguage = getCurrentLanguage()
        var selectedIndex = languageCodes.indexOf(currentLanguage)
        if (selectedIndex == -1) selectedIndex = 0
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_select_language_title))
            .setSingleChoiceItems(languages, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton(getString(R.string.button_select)) { _, _ ->
                changeLanguage(languageCodes[selectedIndex])
                binding.languageSelection.text = languages[selectedIndex]
            }
            .setNegativeButton(getString(R.string.button_cancel), null)
            .show()
    }
    
    private fun getCurrentLanguage(): String {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        return sharedPref.getString("language", Locale.getDefault().language) ?: "en"
    }
    
    private fun changeLanguage(languageCode: String) {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", languageCode)
            apply()
        }
        
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
