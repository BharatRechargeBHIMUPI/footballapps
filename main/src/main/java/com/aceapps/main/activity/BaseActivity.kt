package com.aceapps.main.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.aceapps.main.R
import com.aceapps.main.SharedPref

open class BaseActivity : AppCompatActivity() {
    lateinit var activity: Activity
    lateinit var sharedPref: SharedPref
    lateinit var rootView: View

    fun View.applySystemBarsInsets(bgColor: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setBackgroundColor(bgColor)

            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomInset = if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
                maxOf(systemBars.bottom, ime.bottom)
            } else {
                systemBars.bottom
            }

            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = bottomInset
            )
            insets
        }

        ViewCompat.requestApplyInsets(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = SharedPref(this)
        activity = this

        rootView = findViewById(android.R.id.content)
        rootView.applySystemBarsInsets(
            ContextCompat.getColor(this, R.color.toolbar_bg_color)
        )

    }


}