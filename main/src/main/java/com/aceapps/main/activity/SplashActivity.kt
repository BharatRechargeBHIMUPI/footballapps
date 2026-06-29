package com.aceapps.main.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aceapps.main.AnalyticsHelper
import com.aceapps.main.NetworkUtils
import com.aceapps.main.R
import com.aceapps.main.SubscriptionStatusManagerFactory
import com.aceapps.main.subs.SubscriptionPlanSync
import com.aceapps.main.subs.SubscriptionSubsManager
import com.aceapps.main.subs.SubscriptionSyncPlanFactory
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SplashActivity : BaseActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // ── Original logic (untouched) ─────────────────────────────
        AnalyticsHelper.getInstance().logEvent("event_app_splash_shown", null)
        trackDailyActiveUser()
        if (isFirstInstall()) {
            trackNewInstall()
        }
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        // ── View references ────────────────────────────────────────
        val ring1               = findViewById<View>(R.id.ring1)
        val ring2               = findViewById<View>(R.id.ring2)
        val ring3               = findViewById<View>(R.id.ring3)
        val bgGlow              = findViewById<View>(R.id.bgGlow)
        val gridOverlay         = findViewById<View>(R.id.gridOverlay)
        val logo                = findViewById<ImageView>(R.id.logo)
        val hexRing             = findViewById<ImageView>(R.id.hexRing)
        val logoRing            = findViewById<View>(R.id.logoRing)
        val appName             = findViewById<TextView>(R.id.appName)
        val underline           = findViewById<View>(R.id.underlineAccent)
        val tagline             = findViewById<TextView>(R.id.tagline)
        val dotsLayout          = findViewById<View>(R.id.dotsLayout)
        val dot1                = findViewById<View>(R.id.dot1)
        val dot2                = findViewById<View>(R.id.dot2)
        val dot3                = findViewById<View>(R.id.dot3)
        val poweredBy           = findViewById<TextView>(R.id.poweredBy)
        val loadingBarContainer = findViewById<View>(R.id.loadingBarContainer)
        val loadingBarFill      = findViewById<View>(R.id.loadingBarFill)
        val liveBadge           = findViewById<View>(R.id.liveBadge)
        val liveDot             = findViewById<View>(R.id.liveDot)

        // ══════════════════════════════════════════════════════════
        //  ANIMATION SEQUENCE — all timings/logic identical
        // ══════════════════════════════════════════════════════════

        // STEP 0: Star-field ghost-fades in
        gridOverlay.animate()
            .alpha(0.18f)
            .setDuration(1400)
            .setStartDelay(0)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // STEP 1: Planet/nebula glow blooms top-right
        bgGlow.animate()
            .alpha(0.90f)
            .scaleX(1f).scaleY(1f)
            .setDuration(1000)
            .setStartDelay(80)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        // STEP 2: Pulse rings staggered (now violet)
        startRingPulse(ring1, 0L)
        startRingPulse(ring2, 700L)
        startRingPulse(ring3, 1400L)

        // STEP 3: Live badge drops in top-left
        liveBadge.translationY = -30f
        liveBadge.animate()
            .alpha(1f).translationY(0f)
            .setDuration(500)
            .setStartDelay(150)
            .setInterpolator(OvershootInterpolator(1.6f))
            .withEndAction { startLiveDotPulse(liveDot) }
            .start()

        // STEP 4: Logo icon scales in (bottom-left position)
        val logoScaleUp = AnimatorSet().apply {
            val sx = ObjectAnimator.ofFloat(logo, "scaleX", 0.2f, 1.18f)
            val sy = ObjectAnimator.ofFloat(logo, "scaleY", 0.2f, 1.18f)
            val fa = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
            playTogether(sx, sy, fa)
            duration = 550
            startDelay = 300
            interpolator = DecelerateInterpolator(2f)
        }
        val logoSettles = AnimatorSet().apply {
            val sx = ObjectAnimator.ofFloat(logo, "scaleX", 1.18f, 1f)
            val sy = ObjectAnimator.ofFloat(logo, "scaleY", 1.18f, 1f)
            playTogether(sx, sy)
            duration = 260
            interpolator = OvershootInterpolator(3f)
        }
        AnimatorSet().apply {
            playSequentially(logoScaleUp, logoSettles)
            start()
        }

        // STEP 5: hexRing & logoRing exist but are 1dp — no visible effect
        handler.postDelayed({
            hexRing.animate().alpha(0f).setDuration(1).start()
            startSlowRotation(hexRing)
        }, 700)

        handler.postDelayed({
            logoRing.animate().alpha(0f).setDuration(1).start()
        }, 820)

        // STEP 7: Massive headline slides up
        appName.animate()
            .translationY(0f).alpha(1f)
            .setDuration(700)
            .setStartDelay(950)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        // STEP 8: Accent bar sweeps left → right
        handler.postDelayed({
            val targetPx = (200 * resources.displayMetrics.density).toInt()
            ValueAnimator.ofInt(0, targetPx).apply {
                duration = 600
                interpolator = DecelerateInterpolator(1.5f)
                addUpdateListener { va ->
                    val lp = underline.layoutParams
                    lp.width = va.animatedValue as Int
                    underline.layoutParams = lp
                }
                start()
            }
        }, 1180)

        // STEP 9: Tagline fades in
        tagline.animate()
            .translationY(0f).alpha(1f)
            .setDuration(550)
            .setStartDelay(1280)
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()

        // STEP 10: Loading bar at bottom edge fills up
        handler.postDelayed({
            loadingBarContainer.animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction {
                    // Full screen width in px
                    val targetPx = resources.displayMetrics.widthPixels
                    ValueAnimator.ofInt(0, targetPx).apply {
                        duration = 1800
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener { va ->
                            val lp = loadingBarFill.layoutParams
                            lp.width = va.animatedValue as Int
                            loadingBarFill.layoutParams = lp
                        }
                        start()
                    }
                }
                .start()
        }, 1750)

        // STEP 11: Dots (subtle, near bottom)
        dotsLayout.animate()
            .alpha(1f)
            .setDuration(400)
            .setStartDelay(1850)
            .withEndAction { startDotPulse(dot1, dot2, dot3) }
            .start()

        // STEP 12: Powered-by
        poweredBy.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(2050)
            .start()

        // STEP 13: Breathe pulse on content group
        handler.postDelayed({
            startBreathePulse(findViewById(R.id.contentGroup))
        }, 2300)

        // ── Original subscription check (untouched) ────────────────
        SubscriptionStatusManagerFactory
            .create()
            .init(object : SubscriptionSubsManager.Callback {
                override fun activePurchase(productID: String) {
                    sharedPref.isPremium(true)
                    sharedPref.productID(productID)
                    fetchPlans()
                }
                override fun unActivePurchase() {
                    sharedPref.isPremium(false)
                    sharedPref.productID("")
                    fetchPlans()
                }
            })
    }

    // ══════════════════════════════════════════════════════════════
    //  ANIMATION HELPERS — all identical to original
    // ══════════════════════════════════════════════════════════════

    private fun startRingPulse(ring: View, delay: Long) {
        val scaleX = ObjectAnimator.ofFloat(ring, "scaleX", 0.3f, 2.2f)
        val scaleY = ObjectAnimator.ofFloat(ring, "scaleY", 0.3f, 2.2f)
        val alpha  = ObjectAnimator.ofFloat(ring, "alpha", 0.50f, 0f)
        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration     = 3200
            startDelay   = delay
            interpolator = DecelerateInterpolator(1.5f)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) { start() }
            })
            start()
        }
    }

    private fun startSlowRotation(view: View) {
        ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
            duration     = 14000
            repeatCount  = ObjectAnimator.INFINITE
            repeatMode   = ObjectAnimator.RESTART
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun startBreathePulse(view: View) {
        val scaleUp = AnimatorSet().apply {
            val sx = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.022f)
            val sy = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.022f)
            playTogether(sx, sy)
            duration     = 1900
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleDown = AnimatorSet().apply {
            val sx = ObjectAnimator.ofFloat(view, "scaleX", 1.022f, 1f)
            val sy = ObjectAnimator.ofFloat(view, "scaleY", 1.022f, 1f)
            playTogether(sx, sy)
            duration     = 1900
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playSequentially(scaleUp, scaleDown)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    handler.postDelayed({ startBreathePulse(view) }, 450)
                }
            })
            start()
        }
    }

    private fun startLiveDotPulse(dot: View) {
        ObjectAnimator.ofFloat(dot, "alpha", 0.35f, 1f).apply {
            duration     = 900
            repeatCount  = ObjectAnimator.INFINITE
            repeatMode   = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun startDotPulse(vararg dots: View) {
        dots.forEachIndexed { i, dot ->
            val scaleUp = AnimatorSet().apply {
                val sx = ObjectAnimator.ofFloat(dot, "scaleX", 1f, 1.7f)
                val sy = ObjectAnimator.ofFloat(dot, "scaleY", 1f, 1.7f)
                val fa = ObjectAnimator.ofFloat(dot, "alpha", 0.45f, 1f)
                playTogether(sx, sy, fa)
                duration = 280
            }
            val scaleDown = AnimatorSet().apply {
                val sx = ObjectAnimator.ofFloat(dot, "scaleX", 1.7f, 1f)
                val sy = ObjectAnimator.ofFloat(dot, "scaleY", 1.7f, 1f)
                val fa = ObjectAnimator.ofFloat(dot, "alpha", 1f, 0.45f)
                playTogether(sx, sy, fa)
                duration = 280
            }
            AnimatorSet().apply {
                playSequentially(scaleUp, scaleDown)
                startDelay = i * 220L
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        handler.postDelayed({ startDotPulse(dot) }, 500L)
                    }
                })
                start()
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ORIGINAL LOGIC (untouched)
    // ══════════════════════════════════════════════════════════════

    private fun fetchPlans() {
        SubscriptionSyncPlanFactory
            .create(this)
            .init(object : SubscriptionPlanSync.Callback {
                override fun onPlanFetchedSuccessfully() {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }, 2000)
                }
                override fun onPlanFetchedFail(error: String) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }, 2000)
                }
            })
    }

    private fun trackNewInstall() {
        val db     = FirebaseFirestore.getInstance()
        val userId = getSafeUserId()
        val today  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val data   = hashMapOf("timestamp" to FieldValue.serverTimestamp())
        db.collection("new_installs${getString(R.string.table_prefix)}")
            .document(today).collection("users").document(userId).set(data)
    }

    private fun isFirstInstall(): Boolean {
        val prefs   = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirst = prefs.getBoolean("is_first_install", true)
        if (isFirst) prefs.edit().putBoolean("is_first_install", false).apply()
        return isFirst
    }

    private fun trackDailyActiveUser() {
        val db     = FirebaseFirestore.getInstance()
        val userId = getSafeUserId()
        val today  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val data   = hashMapOf("timestamp" to FieldValue.serverTimestamp())
        db.collection("daily_active_users${getString(R.string.table_prefix)}")
            .document(today).collection("users").document(userId).set(data)
    }

    private fun getSafeUserId(): String {
        return try {
            val androidId = android.provider.Settings.Secure.getString(
                contentResolver, android.provider.Settings.Secure.ANDROID_ID
            )
            if (!androidId.isNullOrEmpty()) androidId else UUID.randomUUID().toString()
        } catch (e: Exception) {
            UUID.randomUUID().toString()
        }
    }
}