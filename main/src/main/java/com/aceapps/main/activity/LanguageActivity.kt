package com.aceapps.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R
import com.aceapps.main.adapter.Language
import com.aceapps.main.adapter.LanguageAdapter

class LanguageActivity : BaseActivity() {

    private lateinit var recyclerLanguages: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: LanguageAdapter

    // True when opened from Settings (Change Language), false when part of onboarding
    private var fromSettings = false

    // Master list — the single source of truth
    private val languages = mutableListOf(

        Language("English (Default)", "🇬🇧", true, "en"),

        // French-speaking
        Language("French", "🇫🇷", code = "fr-FR"),
        Language("French", "🇲🇱", code = "fr-ML"),
        Language("French", "🇸🇳", code = "fr-SN"),
        Language("French", "🇨🇲", code = "fr-CM"),
        Language("French", "🇨🇩", code = "fr-CD"),
        Language("French", "🇬🇦", code = "fr-GA"),
        Language("French", "🇳🇪", code = "fr-NE"),
        Language("French", "🇧🇫", code = "fr-BF"),
        Language("French", "🇲🇺", code = "fr-MU"),

        // Portuguese-speaking
        Language("Portuguese", "🇵🇹", code = "pt-PT"),
        Language("Portuguese", "🇧🇷", code = "pt-BR"),
        Language("Portuguese", "🇦🇴", code = "pt-AO"),

        // Spanish-speaking
        Language("Spanish", "🇪🇸", code = "es-ES"),
        Language("Spanish", "🇨🇴", code = "es-CO"),
        Language("Spanish", "🇪🇨", code = "es-EC"),

        // English-speaking
        Language("English", "🇺🇸", code = "en-US"),
        Language("English", "🇦🇺", code = "en-AU"),
        Language("English", "🇨🇦", code = "en-CA"),
        Language("English", "🇮🇪", code = "en-IE"),
        Language("English", "🇺🇬", code = "en-UG"),
        Language("English", "🇸🇬", code = "en-SG"),
        Language("English", "🇿🇼", code = "en-ZW"),

        // Arabic-speaking
        Language("Arabic", "🇸🇦", code = "ar-SA"),
        Language("Arabic", "🇦🇪", code = "ar-AE"),
        Language("Arabic", "🇲🇦", code = "ar-MA"),

        Language("Hindi", "🇮🇳", code = "hi"),
        Language("Russian", "🇷🇺", code = "ru"),
        Language("Japanese", "🇯🇵", code = "ja"),
        Language("Korean", "🇰🇷", code = "ko"),
        Language("Turkish", "🇹🇷", code = "tr"),
        Language("Italian", "🇮🇹", code = "it"),
        Language("German", "🇩🇪", code = "de-DE"),
        Language("German", "🇨🇭", code = "de-CH"),

        Language("Dutch", "🇳🇱", code = "nl-NL"),
        Language("Dutch", "🇧🇪", code = "nl-BE"),

        Language("Polish", "🇵🇱", code = "pl"),
        Language("Swedish", "🇸🇪", code = "sv"),
        Language("Norwegian", "🇳🇴", code = "nb"),
        Language("Greek", "🇬🇷", code = "el"),
        Language("Greek", "🇨🇾", code = "el-CY"),
        Language("Czech", "🇨🇿", code = "cs"),
        Language("Slovak", "🇸🇰", code = "sk"),
        Language("Slovenian", "🇸🇮", code = "sl"),
        Language("Croatian", "🇭🇷", code = "hr"),
        Language("Romanian", "🇷🇴", code = "ro"),
        Language("Bulgarian", "🇧🇬", code = "bg"),
        Language("Lithuanian", "🇱🇹", code = "lt"),
        Language("Latvian", "🇱🇻", code = "lv"),
        Language("Belarusian", "🇧🇾", code = "be"),
        Language("Luxembourgish", "🇱🇺", code = "lb"),

        Language("Bengali", "🇧🇩", code = "bn"),
        Language("Thai", "🇹🇭", code = "th"),
        Language("Lao", "🇱🇦", code = "lo"),
        Language("Tajik", "🇹🇯", code = "tg"),
        Language("Swahili", "🇹🇿", code = "sw")
    )

    // What's currently shown in the RecyclerView (filtered view of `languages`)
    private var filteredLanguages = mutableListOf<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_language)

        fromSettings = intent.getBooleanExtra("from_settings", false)

        recyclerLanguages = findViewById(R.id.recyclerLanguages)
        etSearch = findViewById(R.id.etSearch)

        // If opened from Settings, pre-select whatever language is currently active
        if (fromSettings) {
            preselectCurrentLanguage()
        }

        filteredLanguages = languages.toMutableList()

        recyclerLanguages.layoutManager = LinearLayoutManager(this)

        adapter = LanguageAdapter(filteredLanguages) { selectedPosition ->
            selectLanguage(selectedPosition)
        }

        recyclerLanguages.adapter = adapter

        etSearch.doAfterTextChanged { text ->
            filterLanguages(text?.toString().orEmpty())
        }

        findViewById<View>(R.id.btnDone).setOnClickListener {

            val selectedLanguage = languages.firstOrNull { it.selected }

            if (selectedLanguage != null) {
                applyLocale(selectedLanguage.code)

                // Optional: also save separately if you need the raw value
                // elsewhere (e.g. showing "Current language: French" in Settings)
                getSharedPreferences("app", MODE_PRIVATE)
                    .edit()
                    .putString("language_code", selectedLanguage.code)
                    .putString("language_name", selectedLanguage.name)
                    .apply()
            }

            if (fromSettings) {
                // Already onboarded — just close and return to Settings
                finish()
            } else {
                // First-time flow — continue onboarding
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
        }
    }

    /**
     * When opened from Settings, mark whichever saved language code matches
     * as selected, so the list opens with the user's current choice highlighted.
     */
    private fun preselectCurrentLanguage() {
        val savedCode = getSharedPreferences("app", MODE_PRIVATE)
            .getString("language_code", null) ?: return

        languages.forEachIndexed { index, language ->
            languages[index] = language.copy(selected = language.code == savedCode)
        }
    }

    /**
     * Switches the app's locale. AppCompatDelegate automatically:
     * - Applies the correct values-xx / values-xx-rYY resources across
     *   the whole app (every Activity, Fragment, Adapter, etc.)
     * - Recreates running activities so they pick up the new locale
     * - Persists the choice so it's restored automatically next app launch
     *   (requires the AppLocalesMetadataHolderService entry in the manifest)
     */
    private fun applyLocale(localeTag: String) {
        val localeList = LocaleListCompat.forLanguageTags(localeTag)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    private fun filterLanguages(query: String) {

        filteredLanguages.clear()

        filteredLanguages.addAll(
            if (query.isBlank()) {
                languages
            } else {
                languages.filter { it.name.contains(query, ignoreCase = true) }
            }
        )

        adapter.notifyDataSetChanged()
    }

    private fun selectLanguage(position: Int) {

        val target = filteredLanguages[position]

        // Update the master list so selection survives filtering/searching
        languages.forEachIndexed { index, language ->
            languages[index] = language.copy(
                selected = language.name == target.name && language.flag == target.flag
            )
        }

        // Refresh the currently filtered view from the updated master list
        val currentQuery = etSearch.text?.toString().orEmpty()
        filterLanguages(currentQuery)
    }
}