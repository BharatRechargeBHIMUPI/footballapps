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
    var products: MutableList<StoreProduct> = mutableListOf()

    private val features = listOf(
        Triple("⚡", "Daily Expert Picks", "Hand-curated predictions delivered every morning"),
        Triple("📊", "In-Depth Match Analysis", "Full stats, form, and head-to-head breakdowns"),
        Triple("🎯", "High Accuracy Tips", "94% average win rate across all VIP predictions"),
    )

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

    /** Fade + slide-left entrance, for stats pills */
    private fun View.animateInFromLeft(delayMs: Long = 0) {
        alpha = 0f
        translationX = -40f
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
     * selected = true → apply selected bg + bounce; false → unselected bg, no bounce.
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

    /** Shake animation for the close button appearing (playful reveal) */
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

    // ─── initData — identical logic, + entrance animations ──────────────────

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

        // ── Staggered entrance sequence ──────────────────────────────────────

        // Hero card
        findViewById<View>(R.id.tvPlanTitle)
            .rootView                              // hero FrameLayout parent (section 2)
            .findViewWithTag<View?>("heroCard")
            ?.animateIn(0) ?: run {
            // fallback: animate the plan title itself
            tvPlanTitle.animateIn(0)
        }

        // Stats row children (section 3) — stagger left-to-right
        val statsRow = (loadingState.parent as? LinearLayout)
            ?.parent as? LinearLayout
        // More reliable: find the stats row directly via layout position
        runStaggeredEntrance()

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
     * Finds each major section by ID and animates them in sequence.
     */
    private fun runStaggeredEntrance() {
        val root = window.decorView

        // Section 2 — hero area (use tvPlanTitle as proxy for that block)
        tvPlanTitle.parent?.let { (it as? View)?.animateIn(0) }

        // Section 3 — stats pills: find the LinearLayout that contains the 3 pills
        // They share the same parent LinearLayout with horizontal orientation.
        // We iterate the plansContainer siblings to find the stats row.
        val plansContainer = findViewById<LinearLayout>(R.id.plansContainer)
        val parentLayout = plansContainer.parent as? LinearLayout ?: return

        for (i in 0 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(i) ?: continue
            val delay = (i * 110).toLong()
            when (i) {
                0 -> child.animateIn(80)             // hero RelativeLayout
                1 -> {                               // stats row LinearLayout
                    val statsRow = child as? LinearLayout ?: continue
                    for (j in 0 until statsRow.childCount) {
                        statsRow.getChildAt(j)?.animateInFromLeft((j * 120 + 160).toLong())
                    }
                }
                2 -> child.animateIn(480)            // features section
                3 -> child.animateIn(600)            // plans container
                4 -> child.animateIn(720)            // CTA + legal
                else -> child.animateIn(delay + 200)
            }
        }
    }

    // ─── initViews — unchanged ───────────────────────────────────────────────

    private fun initViews() {
        tvPlanTitle = findViewById(R.id.tvPlanTitle)
        loadingState = findViewById(R.id.loadingState)
        planWeekly = findViewById(R.id.planWeekly)
        planMonthly = findViewById(R.id.planMonthly)
        btnSubscribe = findViewById(R.id.btnSubscribe)
        tvRestore = findViewById(R.id.tvRestore)
        tvPrivacy = findViewById(R.id.tvPrivacy)
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

    // ─── setupClickListeners — logic unchanged, + card animation + press FX ─

    private fun setupClickListeners() {

        // Attach spring press effect to main tappable surfaces
        btnSubscribe.attachPressEffect()
        btnClose.attachPressEffect()

        if (products.isNotEmpty()) {
            val productWeek = products.firstOrNull { it.id.contains("week") }
            val productMonth = products.firstOrNull { it.id.contains("month") }

            // Always initialize selectedProduct before any click listener fires
            selectedProduct = productMonth ?: productWeek ?: products[0]

            planWeekly.attachPressEffect()
            planWeekly.setOnClickListener {
                productWeek?.let {
                    selectedProduct = it
                    planWeekly.setBackgroundResource(R.drawable.bg_plan_card_selected)
                    planMonthly.setBackgroundResource(R.drawable.bg_plan_card_unselected)
                    planWeekly.animateCardSelect(true)
                    planMonthly.animateCardSelect(false)
                    LaunchFlow(activity, this@SubscriptionActivity).initBillingClient(selectedProduct)
                }
            }

            planMonthly.attachPressEffect()
            planMonthly.setOnClickListener {
                productMonth?.let {
                    selectedProduct = it
                    planWeekly.setBackgroundResource(R.drawable.bg_plan_card_unselected)
                    planMonthly.setBackgroundResource(R.drawable.bg_plan_card_selected)
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

    // ─── displayOffering — logic unchanged, + pop-in on card reveal ─────────

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
