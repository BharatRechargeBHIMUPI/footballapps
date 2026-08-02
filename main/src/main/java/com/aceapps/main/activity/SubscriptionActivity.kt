package com.aceapps.main.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aceapps.main.AnalyticsHelper
import com.aceapps.main.R
import com.aceapps.main.SubscriptionStatusManagerFactory
import com.aceapps.main.subs.LaunchFlow
import com.aceapps.main.subs.ProPlans
import com.aceapps.main.subs.SubscriptionPlanSync
import com.aceapps.main.subs.SubscriptionSubsManager
import com.aceapps.main.subs.SubscriptionSyncPlanFactory
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import kotlin.collections.get


class SubscriptionActivity : BaseActivity(), LaunchFlow.LaunchFlowCallback {

    private lateinit var selectedProduct: StoreProduct

    // Views
    private lateinit var tvPlanTitle: TextView
    private lateinit var loadingState: LinearLayout
    private lateinit var planWeekly: View
    private lateinit var planMonthly: View
    private lateinit var btnSubscribe: View
    private lateinit var btnClose: View
    private lateinit var tvRestore: TextView
    private lateinit var tvPrivacy: TextView

    // New UI-only views
    private lateinit var tvWeeklyRadio: TextView
    private lateinit var tvMonthlyRadio: TextView
    private lateinit var liveDot: View

    var products: MutableList<StoreProduct> = mutableListOf()

    private val features = listOf(
        Triple("⚡", "Daily Expert Picks", "Hand-curated predictions delivered every morning"),
        Triple("📊", "In-Depth Match Analysis", "Full stats, form, and head-to-head breakdowns"),
        Triple("🎯", "High Accuracy Tips", "94% average win rate across all VIP predictions"),
    )

    // Ink colour used on top of the bright green radio
    private val onAccentInk = Color.parseColor("#08240F")

    // ─── Animation helpers ───────────────────────────────────────────────────

    /** Fade + slide-up entrance, with optional start delay for staggering */
    private fun View.animateIn(delayMs: Long = 0) {
        alpha = 0f
        translationY = 60f
        animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(480)
            .setStartDelay(delayMs)
            .setInterpolator(DecelerateInterpolator(1.6f))
            .start()
    }

    /** Fade + slide-left entrance, for form circles and stat columns */
    private fun View.animateInFromLeft(delayMs: Long = 0) {
        alpha = 0f
        translationX = -28f
        animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(400)
            .setStartDelay(delayMs)
            .setInterpolator(DecelerateInterpolator(1.4f))
            .start()
    }

    /** Scale-pop entrance for plan cards */
    private fun View.animatePopIn(delayMs: Long = 0) {
        alpha = 0f
        scaleX = 0.88f
        scaleY = 0.88f
        animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setStartDelay(delayMs)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }

    /** Gentle infinite pulse on a view (e.g. subscribe button) */
    private fun View.startPulse() {
        val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 1.035f, 1f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 1.035f, 1f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }

    /** Slow blink for the "GATE OPEN" indicator dot */
    private fun View.startBlink() {
        ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0.2f, 1f).apply {
            duration = 1800
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * Press-down / release spring effect.
     * Attach to any clickable view — does NOT interfere with click logic.
     */
    private fun View.attachPressEffect() {
        setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.94f).scaleY(0.94f)
                        .setDuration(100)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(220)
                        .setInterpolator(OvershootInterpolator(2f))
                        .start()
                    // Let click still fire
                    if (event.action == MotionEvent.ACTION_UP) v.performClick()
                }
            }
            true
        }
    }

    /**
     * Swap selection with a scale-bounce so the card feels "snappy".
     * selected = true → bounce; false → no bounce.
     */
    private fun View.animateCardSelect(selected: Boolean) {
        if (selected) {
            animate()
                .scaleX(0.96f).scaleY(0.96f)
                .setDuration(80)
                .withEndAction {
                    animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(240)
                        .setInterpolator(OvershootInterpolator(2.5f))
                        .start()
                }
                .start()
        }
    }

    /** Spring reveal for the close button */
    private fun View.animateCloseReveal() {
        scaleX = 0f
        scaleY = 0f
        alpha = 0f
        animate()
            .scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(320)
            .setInterpolator(OvershootInterpolator(3f))
            .start()
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        onBackPressedDispatcher.addCallback(this) {
            // intentionally empty — same as original
        }

        btnClose = findViewById(R.id.btnClose)
        btnClose.setOnClickListener { finish() }

        if (ProPlans.allProduct.isEmpty()) {
            SubscriptionSyncPlanFactory
                .create(this)
                .init(object : SubscriptionPlanSync.Callback {
                    override fun onPlanFetchedSuccessfully() { initData() }
                    override fun onPlanFetchedFail(error: String) {
                        Toast.makeText(
                            activity,
                            "Fail to load subscription, please restart app!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            initData()
        }
    }

    // ─── initData — product logic identical, entrance sequence rebuilt ──────

    private fun initData() {

        var productIntent = intent.getStringExtra("product_name").toString()

        initViews()

        if (intent.getStringExtra("product_name").isNullOrEmpty()) {
            productIntent = "correct_score"
        }

        if (productIntent.contains("correct_score")) {
            products = ProPlans.correctScore
            setupPlanTitle("Correct Score")
        } else if (productIntent.contains("htft")) {
            products = ProPlans.htft
            setupPlanTitle("HTFT")
        } else if (productIntent.contains("ft_draws")) {
            products = ProPlans.ft
            setupPlanTitle("FT Draws")
        } else if (productIntent.contains("ten_odds")) {
            products = ProPlans.tenOdds
            setupPlanTitle("Ten Odds")
        } else if (productIntent.contains("five_odds")) {
            products = ProPlans.fiveOdds
            setupPlanTitle("Five Odds")
        } else if (productIntent.contains("two_odds")) {
            products = ProPlans.twoOdds
            setupPlanTitle("Two Odds")
        } else if (productIntent.contains("mega_subscription")) {
            products = ProPlans.mega_subscription
            setupPlanTitle("Unlock All Sections")
        }

        setupFeatures()
        setupClickListeners()
        displayOffering(products)

        // ── Entrance sequence ────────────────────────────────────────────────
        runStaggeredEntrance()

        liveDot.startBlink()

        // Subscribe button pulse
        btnSubscribe.postDelayed({ btnSubscribe.startPulse() }, 900)

        // Close button with delayed spring reveal (same 2 s delay as original)
        btnClose.visibility = View.GONE
        btnClose.postDelayed({
            btnClose.visibility = View.VISIBLE
            btnClose.animateCloseReveal()
        }, 2000)
    }

    /**
     * Orchestrates the staggered screen entrance.
     * Now driven by IDs instead of child indexes, so re-ordering the layout
     * can never point an animation at the wrong section.
     */
    private fun runStaggeredEntrance() {
        findViewById<View>(R.id.topBar)?.animateIn(0)
        findViewById<View>(R.id.ticketCard)?.animateIn(70)
        findViewById<View>(R.id.featuresSection)?.animateIn(300)
        findViewById<View>(R.id.plansContainer)?.animateIn(400)
        findViewById<View>(R.id.ctaBar)?.animateIn(520)

        // Form guide reveals one result at a time, left to right
        findViewById<LinearLayout>(R.id.formRow)?.let { row ->
            for (j in 0 until row.childCount) {
                row.getChildAt(j)?.animateInFromLeft((j * 70 + 380).toLong())
            }
        }

        // Stat columns on the stub
        findViewById<LinearLayout>(R.id.statsRow)?.let { row ->
            for (j in 0 until row.childCount) {
                row.getChildAt(j)?.animateInFromLeft((j * 80 + 620).toLong())
            }
        }
    }

    // ─── initViews ───────────────────────────────────────────────────────────

    private fun initViews() {
        tvPlanTitle = findViewById(R.id.tvPlanTitle)
        loadingState = findViewById(R.id.loadingState)
        planWeekly = findViewById(R.id.planWeekly)
        planMonthly = findViewById(R.id.planMonthly)
        btnSubscribe = findViewById(R.id.btnSubscribe)
        tvRestore = findViewById(R.id.tvRestore)
        tvPrivacy = findViewById(R.id.tvPrivacy)

        tvWeeklyRadio = findViewById(R.id.tvWeeklyRadio)
        tvMonthlyRadio = findViewById(R.id.tvMonthlyRadio)
        liveDot = findViewById(R.id.liveDot)
    }

    // ─── setupPlanTitle — unchanged ──────────────────────────────────────────

    private fun setupPlanTitle(title: String) {
        tvPlanTitle.text = title
    }

    // ─── setupFeatures — unchanged ───────────────────────────────────────────

    private fun setupFeatures() {
        val featureIds = listOf(R.id.feature1, R.id.feature2, R.id.feature3)
        featureIds.forEachIndexed { index, viewId ->
            val featureView = findViewById<View>(viewId)
            val (icon, title, desc) = features[index]
            featureView.findViewById<TextView>(R.id.tvFeatureIcon).text = icon
            featureView.findViewById<TextView>(R.id.tvFeatureTitle).text = title
            featureView.findViewById<TextView>(R.id.tvFeatureDesc).text = desc
        }
    }

    /** Card background + radio state in one place. Pure UI. */
    private fun applySelection(weeklySelected: Boolean) {
        planWeekly.setBackgroundResource(
            if (weeklySelected) R.drawable.pw_plan_selected else R.drawable.pw_plan_unselected
        )
        planMonthly.setBackgroundResource(
            if (weeklySelected) R.drawable.pw_plan_unselected else R.drawable.pw_plan_selected
        )

        tvWeeklyRadio.setBackgroundResource(
            if (weeklySelected) R.drawable.pw_dot else R.drawable.pw_ring
        )
        tvMonthlyRadio.setBackgroundResource(
            if (weeklySelected) R.drawable.pw_ring else R.drawable.pw_dot
        )

        tvWeeklyRadio.setTextColor(if (weeklySelected) onAccentInk else Color.TRANSPARENT)
        tvMonthlyRadio.setTextColor(if (weeklySelected) Color.TRANSPARENT else onAccentInk)
    }

    // ─── setupClickListeners — billing logic untouched ──────────────────────

    private fun setupClickListeners() {

        // Attach spring press effect to main tappable surfaces
        btnSubscribe.attachPressEffect()
        btnClose.attachPressEffect()

        if (products.isNotEmpty()) {
            val productWeek = products.firstOrNull { it.id.contains("week") }
            val productMonth = products.firstOrNull { it.id.contains("month") }

            // Always initialize selectedProduct before any click listener fires
            selectedProduct = productMonth ?: productWeek ?: products[0]
            applySelection(weeklySelected = productMonth == null)

            planWeekly.attachPressEffect()
            planWeekly.setOnClickListener {
                productWeek?.let {
                    selectedProduct = it
                    applySelection(weeklySelected = true)
                    planWeekly.animateCardSelect(true)
                    planMonthly.animateCardSelect(false)
                    LaunchFlow(activity, this@SubscriptionActivity).initBillingClient(selectedProduct)
                }
            }

            planMonthly.attachPressEffect()
            planMonthly.setOnClickListener {
                productMonth?.let {
                    selectedProduct = it
                    applySelection(weeklySelected = false)
                    planMonthly.animateCardSelect(true)
                    planWeekly.animateCardSelect(false)
                    LaunchFlow(activity, this@SubscriptionActivity).initBillingClient(selectedProduct)
                }
            }

            // Note: btnSubscribe click is handled via attachPressEffect → performClick
            btnSubscribe.setOnClickListener {
                LaunchFlow(activity, this@SubscriptionActivity).initBillingClient(selectedProduct)
            }
        }

        tvRestore.setOnClickListener {
            if (sharedPref.isPremium()) {
                Toast.makeText(activity, "Subscription is Already Active!", Toast.LENGTH_SHORT).show()
            } else {
                SubscriptionStatusManagerFactory
                    .create()
                    .init(object : SubscriptionSubsManager.Callback {
                        override fun activePurchase(productID: String) {
                            sharedPref.isPremium(true)
                            sharedPref.productID(productID)
                            runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "Subscription Restored Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        override fun unActivePurchase() {
                            sharedPref.isPremium(false)
                            sharedPref.productID("")
                            runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "No Active Subscription Found!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }
        }

        tvPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(getString(R.string.privacy_policy_url))
            }
            startActivity(intent)
        }
    }

    // ─── displayOffering — logic unchanged ──────────────────────────────────

    private fun displayOffering(product: MutableList<StoreProduct>) {
        loadingState.visibility = View.GONE

        for (i in product.indices) {
            if (product[i].id.contains("week")) {
                planWeekly.visibility = View.VISIBLE
                planWeekly.animatePopIn(delayMs = 0)
                planWeekly.findViewById<TextView>(R.id.tvWeeklyPrice).text =
                    product[i].price.formatted
            }
            if (product[i].id.contains("month")) {
                planMonthly.visibility = View.VISIBLE
                planMonthly.animatePopIn(delayMs = 120)
                planMonthly.findViewById<TextView>(R.id.tvMonthlyPrice).text =
                    product[i].price.formatted
            }
        }
    }

    // ─── Error / purchase callbacks — unchanged ──────────────────────────────

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSuccessfulPurchase(
        purchase: StoreTransaction,
        productType: String
    ) {
        AnalyticsHelper.getInstance().logEvent("event_app_buy_acknowledge_successful", null)

        val productIdsList: List<String> = purchase.productIds
        var productId: String = ""

        if (productIdsList.isNotEmpty()) {
            productId = productIdsList[0]
        }

        sharedPref.isPremium(true)
        sharedPref.productID(productId)
        ProPlans.activeSubscription.add(productId)
        Toast.makeText(this, "Purchased Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onFailedPurchase() {
        Toast.makeText(this, "Failed to Purchase", Toast.LENGTH_SHORT).show()
    }
}