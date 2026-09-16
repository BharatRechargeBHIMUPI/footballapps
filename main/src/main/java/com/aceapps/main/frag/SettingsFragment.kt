package com.aceapps.main.frag

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.aceapps.main.R
import com.aceapps.main.activity.LanguageActivity
import com.aceapps.main.activity.SubscriptionActivity

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var ivPremium: ImageView
    private lateinit var tvCurrentLanguage: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPremium = view.findViewById(R.id.ivPremium)
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage)

        ivPremium.setOnClickListener {
            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
            intent.putExtra("product_name", "mega_subscription")
            startActivity(intent)
        }

        view.findViewById<View>(R.id.rowVip).setOnClickListener {
            showAlert("VIP Pricing", "🔥 Unlock access to premium tips with our VIP plans!\n\n" +
                    "💎 VIP members get:\n\n" +
                    "✅ Exclusive high-accuracy tips\n" +
                    "⏰ Early match updates\n" +
                    "🎯 Access to premium-only matches\n" +
                    "🚀 Priority support")
        }

        view.findViewById<View>(R.id.rowLanguage).setOnClickListener {
            val intent = Intent(requireActivity(), LanguageActivity::class.java)
            intent.putExtra("from_settings", true)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.rowContact).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("mailto:${getString(R.string.app_email)}")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
            }
            startActivity(intent)
        }

        view.findViewById<View>(R.id.rowFaq).setOnClickListener {
            showAlert("FAQ", "VIP Access & Subscriptions\n\n" +
                    "*What is the purpose of the ${getString(R.string.app_name)}?*\n\n" +
                    "${getString(R.string.app_name)} provides Free and VIP tips to help users make better predictions on matches.\n\n" +
                    "*How do subscriptions work for VIP sections?*\n\n" +
                    "Once you subscribe to a section, this gives you access to that specific VIP section.\n\n" +
                    "*What if I reinstall the app?*\n\n" +
                    "After reinstalling, you can restore VIP access by tapping the subscribed section. No need to re-purchase.\n\n" +
                    "*Can I stop my subscription from renewing?*\n\n" +
                    "Yes, cancel anytime from Google Play Store subscriptions settings.\n\n" +
                    "*Are there any hidden payments or fees?*\n\n" +
                    "No hidden charges — you only pay for the plan you select.\n\n" +
                    "Still stuck? Contact support: ${getString(R.string.app_email)}")
        }

        view.findViewById<View>(R.id.rowFeedback).setOnClickListener {
            showAlert("Feedback", "💬 We'd Love Your Feedback!\n\n" +
                    "Your thoughts help us improve ⚽ ${getString(R.string.app_name)}.\n\n" +
                    "If you have suggestions, questions, or notice something not working, we're here to listen 👇\n\n" +
                    "📧 ${getString(R.string.app_email)}")
        }

        view.findViewById<View>(R.id.rowRate).setOnClickListener {
            val pkg = requireContext().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$pkg")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$pkg")))
            }
        }

        view.findViewById<View>(R.id.rowShare).setOnClickListener {
            val pkg = requireContext().packageName
            val shareText = "🚨 Don't miss easy winning chances!\n\nThis app gives 🔥 daily football predictions with high accuracy.\n\n💰 Many users already winning!\n\nDownload now:\nhttps://play.google.com/store/apps/details?id=$pkg"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        view.findViewById<View>(R.id.rowAbout).setOnClickListener {
            showAlert("About Us", "${getString(R.string.app_name)} is your trusted companion for daily sport tips and match predictions. Get expert advice, stay updated with real-time notifications, and track outcomes with clear win/loss status.")
        }

        view.findViewById<View>(R.id.rowUpdate).setOnClickListener {
            val pkg = requireContext().packageName
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$pkg")))
        }
    }

    override fun onResume() {
        super.onResume()
        updateCurrentLanguageLabel()
    }

    private fun updateCurrentLanguageLabel() {
        val locales = AppCompatDelegate.getApplicationLocales()
        tvCurrentLanguage.text = if (!locales.isEmpty) {
            locales[0]?.displayName ?: getString(R.string.language)
        } else {
            "English" // system default / not yet set
        }
    }

    private fun showAlert(title: String, message: String) {

        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = layoutInflater.inflate(
            R.layout.dialog_custom_alert,
            null
        )

        dialog.setContentView(view)

        val titleView = view.findViewById<TextView>(R.id.dialogTitle)
        val messageView = view.findViewById<TextView>(R.id.dialogMessage)
        val closeView = view.findViewById<ImageView>(R.id.dialogClose)
        val okayView = view.findViewById<TextView>(R.id.dialogOkay)

        titleView.text = title

        messageView.text = message

        closeView.setOnClickListener {
            dialog.dismiss()
        }

        okayView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.apply {

            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setDimAmount(0.65f)

            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            attributes = attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }

        dialog.show()

        dialog.window?.apply {

            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setDimAmount(0.65f)

            val width = (resources.displayMetrics.widthPixels * 0.94).toInt()

            setLayout(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }
}