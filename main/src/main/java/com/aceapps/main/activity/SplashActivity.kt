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
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val lavLoader = findViewById<LottieAnimationView>(R.id.lavLoader)

        lavLoader.setMinFrame(0)
        lavLoader.setMaxFrame(101)
        lavLoader.speed = 1f
        lavLoader.playAnimation()

        // ── Original logic (untouched) ─────────────────────────────
        AnalyticsHelper.getInstance().logEvent("event_app_splash_shown", null)
        trackDailyActiveUser()
        if (isFirstInstall()) {
            trackNewInstall()
        }
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
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
    //  ANIMATION HELPERS
    // ══════════════════════════════════════════════════════════════

    /**
     * Live dot — smooth alpha pulse (mint glow feel).
     * Animates the small circle inside the live badge.
     */


    /** Three-dot staggered pulse loop */

    // ══════════════════════════════════════════════════════════════
    //  ORIGINAL LOGIC (untouched)
    // ══════════════════════════════════════════════════════════════

    private fun fetchPlans() {
        SubscriptionSyncPlanFactory
            .create(this)
            .init(object : SubscriptionPlanSync.Callback {
                override fun onPlanFetchedSuccessfully() {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (sharedPref.isOnBoardingShown()){
                            val intent = Intent(this@SplashActivity, SubscriptionActivity::class.java)
                            intent.putExtra("product_name", "mega_subscription")
                            intent.putExtra("open_main", true)
                            startActivity(intent)
                        }else{
                            startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
                        }
                        finish()
                    }, 2000)
                }
                override fun onPlanFetchedFail(error: String) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (sharedPref.isOnBoardingShown()){
                            val intent = Intent(this@SplashActivity, SubscriptionActivity::class.java)
                            intent.putExtra("product_name", "mega_subscription")
                            intent.putExtra("open_main", true)
                            startActivity(intent)
                        }else{
                            startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
                        }
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
            .document(today)
            .collection("users")
            .document(userId)
            .set(data)
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
            .document(today)
            .collection("users")
            .document(userId)
            .set(data)
    }

    private fun getSafeUserId(): String {
        return try {
            val androidId = android.provider.Settings.Secure.getString(
                contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            if (!androidId.isNullOrEmpty()) androidId
            else UUID.randomUUID().toString()
        } catch (e: Exception) {
            UUID.randomUUID().toString()
        }
    }
}