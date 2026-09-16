package com.aceapps.main.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.aceapps.main.R
import com.aceapps.main.adapter.OnboardingAdapter
import com.aceapps.main.adapter.OnboardingSlide

class OnboardingActivity : BaseActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var btnNext: TextView
    private lateinit var btnSkip: TextView

    private lateinit var slides: List<OnboardingSlide>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        slides = listOf(
            OnboardingSlide(
                R.drawable.onboarding_1,
                getString(R.string.data_driven_match_predictions)
            ),
            OnboardingSlide(
                R.drawable.onboarding_2,
                getString(R.string.head_to_head_form_analysis)
            ),
            OnboardingSlide(
                R.drawable.onboarding_3,
                getString(R.string.free_daily_tips_premium_insights)
            )
        )

        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)

        viewPager.adapter = OnboardingAdapter(slides)

        setupDots()
        setCurrentDot(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentDot(position)
                btnNext.text = if (position == slides.size - 1) getString(R.string.done) else getString(
                    R.string.next
                )
            }
        })

        btnNext.setOnClickListener {
            val next = viewPager.currentItem + 1
            if (next < slides.size) {
                viewPager.currentItem = next
            } else {
                finishOnboarding()
            }
        }

        btnSkip.setOnClickListener { finishOnboarding() }
    }

    private fun setupDots() {
        dotsLayout.removeAllViews()
        for (i in slides.indices) {
            val dot = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 6
            params.marginEnd = 6
            dot.layoutParams = params
            dot.setImageResource(R.drawable.dot_unselected)
            dotsLayout.addView(dot)
        }
    }

    private fun setCurrentDot(position: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == position) R.drawable.dot_selected else R.drawable.dot_unselected
            )
        }
    }

    private fun finishOnboarding() {
        sharedPref.isOnBoardingShown(true)
        val intent = Intent(this, SubscriptionActivity::class.java)
        intent.putExtra("product_name", "mega_subscription")
        intent.putExtra("open_main", true)
        startActivity(intent)
        finish()
    }
}