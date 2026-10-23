
package com.aceapps.main.activity

import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aceapps.main.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationList: LinearLayout
    private lateinit var emptyText: TextView

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFFF5F6FA.toInt())
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setBackgroundColor(0xFFFFFFFF.toInt())
        }

        val back = TextView(this).apply {
            text = "‹"
            textSize = 34f
            setTextColor(0xFF222222.toInt())
            setPadding(0, 0, dp(18), 0)
            setOnClickListener { finish() }
        }

        val heading = TextView(this).apply {
            text = "Notifications"
            textSize = 22f
            setTextColor(0xFF222222.toInt())
            setTypeface(null, Typeface.BOLD)
        }

        header.addView(back)
        header.addView(heading)
        root.addView(header)

        notificationList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val scrollView = ScrollView(this).apply {
            addView(notificationList)
        }

        emptyText = TextView(this).apply {
            text = "No notifications yet"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(0xFF777777.toInt())
            visibility = View.GONE
        }

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            emptyText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(80)
            )
        )

        setContentView(root)

        val dao = AppDatabase.getDatabase(this).notificationDao()

        lifecycleScope.launch {
            dao.getAllNotifications().collectLatest { notifications ->
                notificationList.removeAllViews()
                emptyText.visibility =
                    if (notifications.isEmpty()) View.VISIBLE else View.GONE

                notifications.forEach { notification ->
                    val card = LinearLayout(this@NotificationActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(dp(16), dp(14), dp(16), dp(14))
                        setBackgroundColor(0xFFFFFFFF.toInt())
                        elevation = dp(2).toFloat()
                    }

                    val title = TextView(this@NotificationActivity).apply {
                        text = notification.title
                        textSize = 17f
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(0xFF202124.toInt())
                    }

                    val body = TextView(this@NotificationActivity).apply {
                        text = notification.body
                        textSize = 14f
                        setTextColor(0xFF555555.toInt())
                        setPadding(0, dp(8), 0, 0)
                    }

                    val date = TextView(this@NotificationActivity).apply {
                        text = SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                        ).format(Date(notification.receivedAt))
                        textSize = 12f
                        setTextColor(0xFF888888.toInt())
                        setPadding(0, dp(10), 0, 0)
                    }

                    card.addView(title)
                    card.addView(body)
                    card.addView(date)

                    notificationList.addView(
                        card,
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            bottomMargin = dp(12)
                        }
                    )
                }
            }
        }
    }
}
