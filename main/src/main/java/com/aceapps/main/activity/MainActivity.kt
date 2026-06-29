package com.aceapps.main.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aceapps.main.R
import com.aceapps.main.subs.ProPlans
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity() {

    private lateinit var llDailyMatch : LinearLayout
    private lateinit var vipHistoryCard : MaterialCardView
    private lateinit var tvPrivacy : TextView
    private lateinit var ivNotify : ImageView
    private lateinit var llTelegram : LinearLayout
    private lateinit var btnTodayPicks : MaterialButton
    private lateinit var btnVip : MaterialTextView

    private lateinit var mcvTwoOdds : MaterialCardView
    private lateinit var mcvFTDraws : MaterialCardView
    private lateinit var mcvFiveOdds : MaterialCardView
    private lateinit var mcvTenOdds : MaterialCardView
    private lateinit var mcvCorrectScore : MaterialCardView
    private lateinit var lPro : View
    private lateinit var ivPremium : ImageView
    private lateinit var mcvHTFT : MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
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

        FirebaseMessaging.getInstance().subscribeToTopic("all_users")

        val menuBtn = findViewById<ImageView>(R.id.ivMenu)

        ivPremium = findViewById(R.id.ivPremium)
        lPro = findViewById(R.id.lPro)
        mcvHTFT = findViewById(R.id.mcvHTFT)
        mcvCorrectScore = findViewById(R.id.mcvCorrectScore)
        mcvTenOdds = findViewById(R.id.mcvTenOdds)
        mcvFiveOdds = findViewById(R.id.mcvFiveOdds)
        mcvTwoOdds = findViewById(R.id.mcvTwoOdds)
        mcvFTDraws = findViewById(R.id.mcvFTDraws)

        llTelegram = findViewById(R.id.llTelegram)
        btnVip = findViewById(R.id.btnVip)
        btnTodayPicks = findViewById(R.id.btnTodayPicks)
//        ivNotify = findViewById(R.id.ivNotify)
        llDailyMatch = findViewById(R.id.llDailyMatch)
        vipHistoryCard = findViewById(R.id.vipHistoryCard)
        tvPrivacy = findViewById(R.id.tvPrivacy)

        // ─── ANIMATIONS ────────────────────────────────────────────────────────────
        runEntranceAnimations(menuBtn)
        attachPressAnimations()
        startIdlePulseOnVipCards()
        // ───────────────────────────────────────────────────────────────────────────

        menuBtn.setOnClickListener {
            showMenu(menuBtn)
        }

        mcvFTDraws.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("ft_draws") || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcvFTDraws")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "ft_draws")
                startActivity(intent)

            }
        }

        mcvTwoOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("two_odds")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv2odds")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "two_odds")
                startActivity(intent)
            }

        }

        mcvFiveOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("five_odds")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv5odds")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "five_odds")
                startActivity(intent)
            }
        }

        mcvTenOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("ten_odds")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv10odds")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "ten_odds")
                startActivity(intent)
            }
        }

        mcvCorrectScore.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("correct_score")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcvCorrectVIP")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "correct_score")
                startActivity(intent)
            }
        }

        mcvHTFT.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("htft")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv100VIP")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "htft")
                startActivity(intent)
            }
        }

        tvPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(getString(R.string.privacy_policy_url))
            }

            startActivity(intent)
        }

        llTelegram.setOnClickListener {
            val url = getString(R.string.tele_lnk)

            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
            intent.setPackage("org.telegram.messenger") // force Telegram app

            try {
                startActivity(intent)
            } catch (e: Exception) {
                // If Telegram not installed → open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(browserIntent)
            }
        }

//        ivNotify.setOnClickListener {
//            showNotificationPopup(ivNotify)
//
//        }
        btnVip.setOnClickListener {
            startActivity(Intent(this, VipTipsActivity::class.java))
        }


        lPro.setOnClickListener {
            val intent = Intent(this, SubscriptionActivity::class.java)
            intent.putExtra("product_name", "mega_subscription")
            startActivity(intent)
        }

        ivPremium.setOnClickListener {
            val intent = Intent(this, SubscriptionActivity::class.java)
            intent.putExtra("product_name", "mega_subscription")
            startActivity(intent)
        }

        llDailyMatch.setOnClickListener {
            val intent = Intent(this, DailyMatchesActivity::class.java)
            intent.putExtra("value", "mcvFreeGames")
            startActivity(intent)
        }

        vipHistoryCard.setOnClickListener {
            val intent = Intent(this, VIPHistoryActivity::class.java)
            startActivity(intent)
        }

        btnTodayPicks.setOnClickListener {
            val intent = Intent(this, DailyMatchesActivity::class.java)
            intent.putExtra("value", "mcvFreeGames")
            startActivity(intent)
        }

    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  ANIMATION HELPERS  –  pure visual only, zero business logic
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * Staggered entrance: header slides in, then cards fly up one by one.
     * Every view starts invisible (alpha=0) in the XML so there's no flash.
     */
    private fun runEntranceAnimations(menuBtn: View) {
        val overshoot = OvershootInterpolator(1.4f)
        val decelerate = DecelerateInterpolator(2f)

        // --- Header items slide in (left icon, title, right icon) ---
        val titleGroup = findViewById<View>(R.id.titleGroup)
        val headerDelay = 80L

        animateIn(menuBtn,         translationXFrom = -30f, delay = 0L,           interp = decelerate)
        animateIn(titleGroup,      translationYFrom = -20f, delay = headerDelay,   interp = overshoot)
        animateIn(ivPremium,       translationXFrom = 30f,  delay = headerDelay * 2, interp = decelerate)

        // --- Promo card drops in with a bounce ---
        lPro.postDelayed({
            lPro.alpha = 0f
            lPro.translationY = -60f
            ObjectAnimator.ofFloat(lPro, "alpha", 0f, 1f).apply { duration = 500 }.start()
            ObjectAnimator.ofFloat(lPro, "translationY", -60f, 0f).apply {
                duration = 600
                interpolator = BounceInterpolator()
            }.start()
        }, 200)

        // --- Free + History cards slide up ---
        val freeCard = findViewById<View>(R.id.freeMatchesCard)
        animateIn(freeCard,       translationYFrom = 40f, delay = 350L, interp = overshoot)
        animateIn(vipHistoryCard, translationYFrom = 40f, delay = 450L, interp = overshoot)

        // --- Section label pops in with scale ---
        val sectionLabel = tvSection2Label()
        sectionLabel?.let {
            it.postDelayed({
                ObjectAnimator.ofFloat(it, "alpha",  0f, 1f).apply { duration = 400 }.start()
                ObjectAnimator.ofFloat(it, "scaleX", 0.7f, 1f).apply { duration = 450; interpolator = overshoot }.start()
                ObjectAnimator.ofFloat(it, "scaleY", 0.7f, 1f).apply { duration = 450; interpolator = overshoot }.start()
            }, 550)
        }

        // --- VIP grid cards slide in from alternating sides ---
        val vipCards = listOf(mcvFTDraws, mcvTwoOdds, mcvFiveOdds, mcvTenOdds, mcvCorrectScore, mcvHTFT)
        vipCards.forEachIndexed { index, card ->
            val fromX = if (index % 2 == 0) -50f else 50f
            animateIn(card, translationXFrom = fromX, delay = 620L + index * 80L, interp = overshoot)
        }

        // --- Telegram card and privacy slide up last ---
        val telegramCard = findViewById<View>(R.id.joinTelegramCard)
        animateIn(telegramCard, translationYFrom = 30f, delay = 1100L, interp = overshoot)
    }

    /** Finds the LinearLayout parent of tvSection2 (the "EXCLUSIVE TIPS" label group) */
    private fun tvSection2Label(): View? {
        return try {
            (findViewById<TextView>(R.id.tvSection2)).parent as? View
        } catch (e: Exception) { null }
    }

    /**
     * Fade + translate entrance for a single view.
     */
    private fun animateIn(
        view: View,
        translationXFrom: Float = 0f,
        translationYFrom: Float = 0f,
        delay: Long = 0L,
        duration: Long = 500L,
        interp: android.view.animation.Interpolator = DecelerateInterpolator()
    ) {
        view.postDelayed({
            val animators = mutableListOf<Animator>()
            animators += ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply { this.duration = duration }
            if (translationXFrom != 0f) {
                animators += ObjectAnimator.ofFloat(view, "translationX", translationXFrom, 0f).apply {
                    this.duration = duration; interpolator = interp
                }
            }
            if (translationYFrom != 0f) {
                animators += ObjectAnimator.ofFloat(view, "translationY", translationYFrom, 0f).apply {
                    this.duration = duration; interpolator = interp
                }
            }
            AnimatorSet().apply { playTogether(animators); start() }
        }, delay)
    }

    /**
     * Press-down / release spring animation on every tappable card.
     * Gives a satisfying "chip pressed on casino table" tactile feel.
     */
    private fun attachPressAnimations() {
        val pressTargets: List<View> = listOf(
            mcvFTDraws, mcvTwoOdds, mcvFiveOdds, mcvTenOdds,
            mcvCorrectScore, mcvHTFT,
            vipHistoryCard,
            findViewById(R.id.freeMatchesCard),
            findViewById(R.id.joinTelegramCard),
            lPro, ivPremium
        )

        pressTargets.forEach { view ->
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        AnimatorSet().apply {
                            playTogether(
                                ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.94f).apply { duration = 120 },
                                ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.94f).apply { duration = 120 },
                                ObjectAnimator.ofFloat(v, "alpha",  1f, 0.80f).apply { duration = 120 }
                            )
                            start()
                        }
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        AnimatorSet().apply {
                            playTogether(
                                ObjectAnimator.ofFloat(v, "scaleX", 0.94f, 1f).apply {
                                    duration = 350; interpolator = OvershootInterpolator(3f)
                                },
                                ObjectAnimator.ofFloat(v, "scaleY", 0.94f, 1f).apply {
                                    duration = 350; interpolator = OvershootInterpolator(3f)
                                },
                                ObjectAnimator.ofFloat(v, "alpha", 0.80f, 1f).apply { duration = 200 }
                            )
                            start()
                        }
                    }
                }
                false // pass event through so click listeners still fire
            }
        }
    }

    /**
     * Subtle never-ending heartbeat pulse on VIP grid cards —
     * like a neon sign flickering. Each card pulses with a slight offset
     * so they don't all throb together (casino light board effect).
     */
    private fun startIdlePulseOnVipCards() {
        val vipCards = listOf(mcvFTDraws, mcvTwoOdds, mcvFiveOdds, mcvTenOdds, mcvCorrectScore, mcvHTFT)
        vipCards.forEachIndexed { index, card ->
            card.postDelayed({
                val pulseX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 1.03f, 1f).apply {
                    duration = 1800
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                val pulseY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 1.03f, 1f).apply {
                    duration = 1800
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                AnimatorSet().apply { playTogether(pulseX, pulseY); start() }
            }, 1400L + index * 220L) // staggered start after entrance finishes
        }

        // Bell / premium icon gets a gentle rotate-wiggle to attract attention
        val bellWiggle = ObjectAnimator.ofFloat(ivPremium, "rotation", 0f, 12f, -12f, 8f, -8f, 4f, 0f).apply {
            duration = 1200
            startDelay = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            // wiggle every ~6 seconds total (1.2s anim + 4.8s pause via repeat delay workaround)
        }
        // Add idle gap between wiggles using an AnimatorListenerAdapter
        bellWiggle.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                animation.startDelay = 5000 // pause between wiggles
            }
        })
        bellWiggle.start()
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  ORIGINAL METHODS BELOW — NOT TOUCHED
    // ═══════════════════════════════════════════════════════════════════════════════

    private fun showNotificationPopup(anchor: View) {

        val view = LayoutInflater.from(this).inflate(R.layout.popup_notifications, null)

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.elevation = 20f
        popupWindow.isOutsideTouchable = true

        // Show below bell icon
        popupWindow.showAsDropDown(anchor, -200, 20)

        // Close button
        view.findViewById<View>(R.id.btnClose).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun showMenu(anchor: View) {
        val view = LayoutInflater.from(this).inflate(R.layout.menu_bottom_sheet, null)

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.elevation = 10f
        popupWindow.isOutsideTouchable = true

        // Show near menu icon
        popupWindow.showAsDropDown(anchor, 0, 10)

        // Click listeners
        view.findViewById<View>(R.id.menuVip).setOnClickListener {
            popupWindow.dismiss()
            showAlert("VIP Pricing", "\uD83D\uDD25 Unlock access to premium tips with our VIP plans!\n" +
                    "\n" +
                    "\uD83D\uDC8E VIP members get:\n" +
                    "\n" +
                    "✅ Exclusive high-accuracy tips  \n" +
                    "⏰ Early match updates  \n" +
                    "\uD83C\uDFAF Access to premium-only matches  \n" +
                    "\uD83D\uDE80 Priority support  ")

        }

        view.findViewById<View>(R.id.menuContact).setOnClickListener {
            popupWindow.dismiss()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("mailto:${getString(R.string.app_email)}") // your email
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
            }

            startActivity(intent)

        }

        view.findViewById<View>(R.id.menuUpdateApp).setOnClickListener {
            popupWindow.dismiss()

            val appPackageName = packageName

            val intent = Intent(
                Intent.ACTION_VIEW,
                android.net.Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )

            startActivity(intent)
        }

        view.findViewById<View>(R.id.menuRate).setOnClickListener {
            popupWindow.dismiss()

            val appPackageName = packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        android.net.Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (e: Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }

        view.findViewById<View>(R.id.menuShare).setOnClickListener {
            popupWindow.dismiss()

            val appPackageName = packageName
            val shareText = "🚨 Don't miss easy winning chances!\n\nThis app gives 🔥 daily football predictions with high accuracy.\n\n💰 Many users already winning!\n\nDownload now:\nhttps://play.google.com/store/apps/details?id=$appPackageName"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(intent, "Share via"))
        }

        view.findViewById<View>(R.id.menuFeedback).setOnClickListener {
            popupWindow.dismiss()
            showAlert("Feedback", "\uD83D\uDCAC We'd Love Your Feedback!\n" +
                    "\n" +
                    "Your thoughts help us improve ⚽ ${getString(R.string.app_name)}.\n" +
                    "\n" +
                    "If you have any suggestions, questions, or notice something not working, we're here to listen \uD83D\uDC47\n" +
                    "\n" +
                    "\uD83D\uDCE7 ${getString(R.string.app_email)}")
        }

        view.findViewById<View>(R.id.menuAbout).setOnClickListener {
            popupWindow.dismiss()
            showAlert("About Us", "${getString(R.string.app_name)} is your trusted companion for daily sport tips and match predictions. Get expert advice, stay updated with real-time notifications, and track outcomes with clear win/ loss status. Designed for simplicity and performance, ${
                getString(
                    R.string.app_name
                )
            } helps you make smarter decisions - all in one easy-to-use app.")

        }

        view.findViewById<View>(R.id.menuFaq).setOnClickListener {
            popupWindow.dismiss()
            showAlert("FAQ", "VIP Access & Subscriptions\n" +
                    "\n" +
                    "*What is the purpose of the ${getString(R.string.app_name)}?*\n" +
                    "\n" +
                    "${getString(R.string.app_name)} provides Free and VIP tips to help users make better predictions on matches.\n" +
                    "\n" +
                    "*How do subscriptions work for VIP sections?*\n" +
                    "\n" +
                    "Once you subscribe to a section. This gives vou access to that specific VIP section. \n" +
                    "\n" +
                    "*What if I reinstall the app?*\n" +
                    "\n" +
                    "After reinstalling the app, you can restore your VIP access by tapping the subscribed section. No need to re-purchase.\n" +
                    "\n" +
                    "*Can I stop my subscription from renewing?*\n" +
                    "\n" +
                    "Yes, you can cancel or stop renewal anytime directly from your Google Play Store subscriptions settings.\n" +
                    "\n" +
                    "*Are there any hidden payments or fees?*\n" +
                    "\n" +
                    "No, there are no hidden charges. You only pay for the subscription plan you select.\n" +
                    "\n" +
                    "If the problem persists, contact support: ${getString(R.string.app_email)}")
        }
    }

    private fun showAlert(title: String, message: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

}