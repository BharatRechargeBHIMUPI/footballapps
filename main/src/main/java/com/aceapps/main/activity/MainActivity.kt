package com.aceapps.main.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aceapps.main.R
import com.aceapps.main.frag.HomeFragment
import com.aceapps.main.frag.SettingsFragment
import com.aceapps.main.frag.TipsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase anonymous login
        if (!sharedPref.isUserSignIn()) {
            FirebaseAuth.getInstance()
                .signInAnonymously()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        sharedPref.isUserSignIn(true)
                    } else {
                        println("Anonymous auth failed: ${task.exception}")
                    }
                }
        }

        // Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // Firebase topic
        FirebaseMessaging.getInstance()
            .subscribeToTopic("all_users")


        // Open Home when Activity starts
        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }


        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    openFragment(HomeFragment())
                    true
                }

                R.id.nav_tips -> {
                    openFragment(TipsFragment())
                    true
                }

                R.id.nav_settings -> {
                    openFragment(SettingsFragment())
                    true
                }

                R.id.nav_telegram -> {
                    openTelegramChannel()
                    false
                }

                else -> false
            }
        }
    }

    private fun openTelegramChannel() {

        val telegramUrl = getString(R.string.tele_lnk)

        try {
            // Open Telegram app if installed
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(telegramUrl)
                )
            )
        } catch (e: Exception) {
            // Fallback to browser
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(telegramUrl)
                )
            )
        }
    }


    /**
     * Always opens a completely NEW Fragment.
     *
     * The previous Fragment is removed from the container.
     */
    private fun openFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }
}