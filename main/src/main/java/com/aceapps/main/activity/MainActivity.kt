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
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aceapps.main.R
import com.aceapps.main.subs.ProPlans
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity() {

    private lateinit var llDailyMatch: LinearLayout
    private lateinit var vipHistoryCard: MaterialCardView
    private lateinit var llText: LinearLayoutCompat
    private lateinit var tvPrivacy: TextView
    private lateinit var ivNotify: ImageView
    private lateinit var llTelegram: LinearLayout
    private lateinit var btnTodayPicks: MaterialButton
    private lateinit var btnVip: MaterialButton

    private lateinit var mcvTwoOdds: MaterialCardView
    private lateinit var mcvFTDraws: MaterialCardView
    private lateinit var mcvFiveOdds: MaterialCardView
    private lateinit var mcvTenOdds: MaterialCardView
    private lateinit var mcvCorrectScore: MaterialCardView
    private lateinit var lPro: View
    private lateinit var ivPremium: ImageView
    private lateinit var mcvHTFT: MaterialCardView

    // ── Contact links fetched from Firestore ──────────────────────────────────
    private var whatsappLink: String = "https://wa.me/2348142830549"   // fallback
    private var telegramLink: String = "https://t.me/Football_Insidar" // fallback

    // ── Per-section admin passwords (kept server-side ideally; hardcoded per requirement) ──
    private val PASSWORD_2ODDS = "2Odd@@"
    private val PASSWORD_5ODDS = "5Odd@1"
    private val PASSWORD_10ODDS = "10Odds@@"
    private val PASSWORD_CORRECT_SCORE = "Correctscore1@"
    private val PASSWORD_HTFT = "HTFT1\$@"
    private val PASSWORD_FT_DRAWS = "Ftdraw1@"

    // ── Tracks whether each section is "unlocked" in this session ────────────
    private var isUnlocked2Odds = false
    private var isUnlocked5Odds = false
    private var isUnlocked10Odds = false
    private var isUnlockedCorrectScore = false
    private var isUnlockedHTFT = false
    private var isUnlockedFTDraws = false

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

        // ── Fetch contact links from Firestore ───────────────────────────────
        fetchContactLinks()

        val menuBtn = findViewById<ImageView>(R.id.ivMenu)

        llText = findViewById(R.id.llText)
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
        llDailyMatch = findViewById(R.id.llDailyMatch)
        vipHistoryCard = findViewById(R.id.vipHistoryCard)
        tvPrivacy = findViewById(R.id.tvPrivacy)

        // ── Animations ───────────────────────────────────────────────────────
        runEntranceAnimations(menuBtn)
        attachPressAnimations()
        startIdlePulseOnVipCards()

        // ── Menu ─────────────────────────────────────────────────────────────
        menuBtn.setOnClickListener { showMenu(menuBtn) }

        // ── VIP cards → each one gates on its own password ───────────────────
        llText.setOnClickListener     { openLink(whatsappLink) }
        mcvFTDraws.setOnClickListener      { handleFTDrawsClick() }
        mcvTwoOdds.setOnClickListener      { handleTwoOddsClick() }
        mcvFiveOdds.setOnClickListener     { handleFiveOddsClick() }
        mcvTenOdds.setOnClickListener      { handleTenOddsClick() }
        mcvCorrectScore.setOnClickListener { handleCorrectScoreClick() }
        mcvHTFT.setOnClickListener         { handleHTFTClick() }

        // ── lPro / ivPremium → still uses contact dialog (unaffected) ────────
        lPro.setOnClickListener      { showContactDialog() }
        ivPremium.setOnClickListener { showContactDialog() }

        // ── Free / public sections ────────────────────────────────────────────
        tvPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(getString(R.string.privacy_policy_url))
            }
            startActivity(intent)
        }

        llTelegram.setOnClickListener {
            openLink(telegramLink, "org.telegram.messenger")
        }

        btnVip.setOnClickListener {
            startActivity(Intent(this, VipTipsActivity::class.java))
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

    // ═══════════════════════════════════════════════════════════════════════════
    //  FIRESTORE
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Reads the `contact_admin` document from Firestore and caches the links.
     * Falls back to the hardcoded defaults if the read fails.
     *
     * Firestore structure expected:
     *   Collection : contact_admin
     *   Document   : links          ← (any document ID works; adjust below if needed)
     *   Fields     : whatsapp (String), telegram (String)
     */
    private fun fetchContactLinks() {
        FirebaseFirestore.getInstance()
            .collection("contact_admin")
            .document("links")  // change this document ID if yours differs
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot?.getString("whatsapp")?.takeIf { it.isNotBlank() }?.let {
                    whatsappLink = it
                }
                snapshot?.getString("telegram")?.takeIf { it.isNotBlank() }?.let {
                    telegramLink = it
                }
            }
            .addOnFailureListener {
                // silently keep fallback values
            }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CONTACT DIALOG  –  shown when lPro / ivPremium is tapped
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Inflates a custom dialog with two styled buttons:
     *   • WhatsApp  (green)
     *   • Telegram  (blue)
     * Links come from Firestore (or the fallback constants above).
     */
    private fun showContactDialog() {
        // Build a simple programmatic layout so we don't need an extra XML file.
        val ctx = this

        val titleView = TextView(ctx).apply {
            text = "🔐 Get VIP Access"
            textSize = 18f
            setPadding(48, 48, 48, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subView = TextView(ctx).apply {
            text = "Contact us on WhatsApp or Telegram to subscribe and unlock VIP tips."
            textSize = 14f
            setPadding(48, 8, 48, 32)
        }

        val btnWhatsApp = com.google.android.material.button.MaterialButton(ctx).apply {
            text = "💬  WhatsApp"
            setBackgroundColor(android.graphics.Color.parseColor("#25D366"))
            setTextColor(android.graphics.Color.WHITE)
            cornerRadius = 32
        }

        val btnTelegram = com.google.android.material.button.MaterialButton(ctx).apply {
            text = "✈️  Telegram"
            setBackgroundColor(android.graphics.Color.parseColor("#0088CC"))
            setTextColor(android.graphics.Color.WHITE)
            cornerRadius = 32
        }

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(48, 8, 48, 8) }

        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            addView(titleView)
            addView(subView)
            addView(btnWhatsApp, lp)
            addView(btnTelegram, lp)
            val bottomPad = LinearLayout.LayoutParams(0, 24)
            addView(View(ctx), bottomPad)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame)

        btnWhatsApp.setOnClickListener {
            dialog.dismiss()
            openLink(whatsappLink)
        }

        btnTelegram.setOnClickListener {
            dialog.dismiss()
            openLink(telegramLink, "org.telegram.messenger")
        }

        dialog.show()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  PER-CARD PASSWORD UNLOCK  –  each VIP card has its own password & target
    // ═══════════════════════════════════════════════════════════════════════════

    private fun handleFTDrawsClick() {
        if (isUnlockedFTDraws) {
            openDailyMatches("mcvFTDraws")
        } else {
            showPasswordDialog(
                correctPassword = PASSWORD_FT_DRAWS,
                onSuccess = {
                    isUnlockedFTDraws = true
                    openDailyMatches("mcvFTDraws")
                }
            )
        }
    }

    private fun handleTwoOddsClick() {
        if (isUnlocked2Odds) {
            openDailyMatches("mcv2odds")
        } else {
            showPasswordDialog(
                correctPassword = PASSWORD_2ODDS,
                onSuccess = {
                    isUnlocked2Odds = true
                    openDailyMatches("mcv2odds")
                }
            )
        }
    }

    private fun handleFiveOddsClick() {
        if (isUnlocked5Odds) {
            openDailyMatches("mcv5odds")
        } else {
            showPasswordDialog(
                correctPassword = PASSWORD_5ODDS,
                onSuccess = {
                    isUnlocked5Odds = true
                    openDailyMatches("mcv5odds")
                }
            )
        }
    }

    private fun handleTenOddsClick() {
        if (isUnlocked10Odds) {
            openDailyMatches("mcv10odds")
        } else {
            showPasswordDialog(
                correctPassword = PASSWORD_10ODDS,
                onSuccess = {
                    isUnlocked10Odds = true
                    openDailyMatches("mcv10odds")
                }
            )
        }
    }

    private fun handleCorrectScoreClick() {
        if (isUnlockedCorrectScore) {
            openDailyMatches("mcvCorrectVIP")
        } else {
            showPasswordDialog(
                correctPassword = PASSWORD_CORRECT_SCORE,
                onSuccess = {
                    isUnlockedCorrectScore = true
                    openDailyMatches("mcvCorrectVIP")
                }
            )
        }
    }

    private fun handleHTFTClick() {
        if (isUnlockedHTFT) {
            openDailyMatches("mcv100VIP")
        } else {
            showPasswordDialog(
                correctPassword = PASSWORD_HTFT,
                onSuccess = {
                    isUnlockedHTFT = true
                    openDailyMatches("mcv100VIP")
                }
            )
        }
    }

    /** Opens DailyMatchesActivity with the given "value" extra. */
    private fun openDailyMatches(value: String) {
        startActivity(Intent(this, DailyMatchesActivity::class.java).putExtra("value", value))
    }

    /**
     * Generic password dialog. Checks the entered text against [correctPassword].
     * On success: dismisses the dialog, shows a toast, and runs [onSuccess].
     * On failure: shows an error toast and keeps the dialog open for retry.
     */
    private fun showPasswordDialog(correctPassword: String, onSuccess: () -> Unit) {
        val input = EditText(this).apply {
            hint = "Enter password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(48, 32, 48, 32)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("🔑 VIP Access")
            .setMessage("Enter the password to unlock this section.")
            .setView(input)
            .setPositiveButton("Unlock", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            // Override the positive button so an incorrect password doesn't close the dialog.
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val entered = input.text.toString().trim()
                if (entered == correctPassword) {
                    dialog.dismiss()
                    Toast.makeText(this, "✅ Unlocked!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(this, "❌ Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  UTILITY
    // ═══════════════════════════════════════════════════════════════════════════

    /** Opens a URL, optionally forcing a specific package (e.g. Telegram). */
    private fun openLink(url: String, packageName: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
        packageName?.let { intent.setPackage(it) }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // App not installed → fall back to browser
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  ANIMATION HELPERS  –  pure visual only, zero business logic
    // ═══════════════════════════════════════════════════════════════════════════

    private fun runEntranceAnimations(menuBtn: View) {
        val overshoot = OvershootInterpolator(1.4f)
        val decelerate = DecelerateInterpolator(2f)

        val titleGroup = findViewById<View>(R.id.titleGroup)
        val headerDelay = 80L

        animateIn(menuBtn,    translationXFrom = -30f, delay = 0L,             interp = decelerate)
        animateIn(titleGroup, translationYFrom = -20f, delay = headerDelay,    interp = overshoot)
        animateIn(ivPremium,  translationXFrom = 30f,  delay = headerDelay * 2, interp = decelerate)

        lPro.postDelayed({
            lPro.alpha = 0f
            lPro.translationY = -60f
            ObjectAnimator.ofFloat(lPro, "alpha", 0f, 1f).apply { duration = 500 }.start()
            ObjectAnimator.ofFloat(lPro, "translationY", -60f, 0f).apply {
                duration = 600; interpolator = BounceInterpolator()
            }.start()
        }, 200)

        val freeCard = findViewById<View>(R.id.freeMatchesCard)
        animateIn(freeCard,       translationYFrom = 40f, delay = 350L, interp = overshoot)
        animateIn(vipHistoryCard, translationYFrom = 40f, delay = 450L, interp = overshoot)

        tvSection2Label()?.let {
            it.postDelayed({
                ObjectAnimator.ofFloat(it, "alpha",  0f, 1f).apply { duration = 400 }.start()
                ObjectAnimator.ofFloat(it, "scaleX", 0.7f, 1f).apply { duration = 450; interpolator = overshoot }.start()
                ObjectAnimator.ofFloat(it, "scaleY", 0.7f, 1f).apply { duration = 450; interpolator = overshoot }.start()
            }, 550)
        }

        val vipCards = listOf(mcvFTDraws, mcvTwoOdds, mcvFiveOdds, mcvTenOdds, mcvCorrectScore, mcvHTFT)
        vipCards.forEachIndexed { index, card ->
            val fromX = if (index % 2 == 0) -50f else 50f
            animateIn(card, translationXFrom = fromX, delay = 620L + index * 80L, interp = overshoot)
        }

        val telegramCard = findViewById<View>(R.id.joinTelegramCard)
        animateIn(telegramCard, translationYFrom = 30f, delay = 1100L, interp = overshoot)
    }

    private fun tvSection2Label(): View? {
        return try {
            (findViewById<TextView>(R.id.tvSection2)).parent as? View
        } catch (e: Exception) { null }
    }

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
                false
            }
        }
    }

    private fun startIdlePulseOnVipCards() {
        val vipCards = listOf(mcvFTDraws, mcvTwoOdds, mcvFiveOdds, mcvTenOdds, mcvCorrectScore, mcvHTFT)
        vipCards.forEachIndexed { index, card ->
            card.postDelayed({
                val pulseX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 1.03f, 1f).apply {
                    duration = 1800; repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                val pulseY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 1.03f, 1f).apply {
                    duration = 1800; repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                AnimatorSet().apply { playTogether(pulseX, pulseY); start() }
            }, 1400L + index * 220L)
        }

        val bellWiggle = ObjectAnimator.ofFloat(ivPremium, "rotation", 0f, 12f, -12f, 8f, -8f, 4f, 0f).apply {
            duration = 1200; startDelay = 2000
            repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.RESTART
        }
        bellWiggle.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                animation.startDelay = 5000
            }
        })
        bellWiggle.start()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  ORIGINAL METHODS  –  NOT TOUCHED
    // ═══════════════════════════════════════════════════════════════════════════

    private fun showNotificationPopup(anchor: View) {
        val view = LayoutInflater.from(this).inflate(R.layout.popup_notifications, null)
        val popupWindow = PopupWindow(
            view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )
        popupWindow.elevation = 20f
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(anchor, -200, 20)
        view.findViewById<View>(R.id.btnClose).setOnClickListener { popupWindow.dismiss() }
    }

    private fun showMenu(anchor: View) {
        val view = LayoutInflater.from(this).inflate(R.layout.menu_bottom_sheet, null)
        val popupWindow = PopupWindow(
            view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )
        popupWindow.elevation = 10f
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(anchor, 0, 10)

        view.findViewById<View>(R.id.menuVip).setOnClickListener {
            popupWindow.dismiss()
            showAlert("VIP Pricing", "\uD83D\uDD25 Unlock access to premium tips with our VIP plans!\n\n\uD83D\uDC8E VIP members get:\n\n✅ Exclusive high-accuracy tips  \n⏰ Early match updates  \n\uD83C\uDFAF Access to premium-only matches  \n\uD83D\uDE80 Priority support  ")
        }

        view.findViewById<View>(R.id.menuContact).setOnClickListener {
            popupWindow.dismiss()
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("mailto:${getString(R.string.app_email)}")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
            }
            startActivity(intent)
        }

        view.findViewById<View>(R.id.menuUpdateApp).setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            )
        }

        view.findViewById<View>(R.id.menuRate).setOnClickListener {
            popupWindow.dismiss()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        view.findViewById<View>(R.id.menuShare).setOnClickListener {
            popupWindow.dismiss()
            val shareText = "🚨 Don't miss easy winning chances!\n\nThis app gives 🔥 daily football predictions with high accuracy.\n\n💰 Many users already winning!\n\nDownload now:\nhttps://play.google.com/store/apps/details?id=$packageName"
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, shareText)
            }, "Share via"))
        }

        view.findViewById<View>(R.id.menuFeedback).setOnClickListener {
            popupWindow.dismiss()
            showAlert("Feedback", "\uD83D\uDCAC We'd Love Your Feedback!\n\nYour thoughts help us improve ⚽ ${getString(R.string.app_name)}.\n\nIf you have any suggestions, questions, or notice something not working, we're here to listen \uD83D\uDC47\n\n\uD83D\uDCE7 ${getString(R.string.app_email)}")
        }

        view.findViewById<View>(R.id.menuAbout).setOnClickListener {
            popupWindow.dismiss()
            showAlert("About Us", "${getString(R.string.app_name)} is your trusted companion for daily sport tips and match predictions. Get expert advice, stay updated with real-time notifications, and track outcomes with clear win/ loss status. Designed for simplicity and performance, ${getString(R.string.app_name)} helps you make smarter decisions - all in one easy-to-use app.")
        }

        view.findViewById<View>(R.id.menuFaq).setOnClickListener {
            popupWindow.dismiss()
            showAlert("FAQ", "VIP Access & Subscriptions\n\n*What is the purpose of the ${getString(R.string.app_name)}?*\n\n${getString(R.string.app_name)} provides Free and VIP tips to help users make better predictions on matches.\n\n*How do subscriptions work for VIP sections?*\n\nOnce you subscribe to a section. This gives vou access to that specific VIP section. \n\n*What if I reinstall the app?*\n\nAfter reinstalling the app, you can restore your VIP access by tapping the subscribed section. No need to re-purchase.\n\n*Can I stop my subscription from renewing?*\n\nYes, you can cancel or stop renewal anytime directly from your Google Play Store subscriptions settings.\n\n*Are there any hidden payments or fees?*\n\nNo, there are no hidden charges. You only pay for the subscription plan you select.\n\nIf the problem persists, contact support: ${getString(R.string.app_email)}")
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}