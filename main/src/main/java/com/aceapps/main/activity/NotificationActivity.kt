
package com.aceapps.main.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    private var notificationListener: ListenerRegistration? = null

    private val dateFormat = SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.WHITE)
        }

        val header = TextView(this).apply {
            text = "Notifications"
            textSize = 24f
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(20), dp(20), dp(20), dp(16))
        }

        progressBar = ProgressBar(this).apply {
            visibility = View.VISIBLE
        }

        emptyText = TextView(this).apply {
            text = "No notifications yet"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.GRAY)
            visibility = View.GONE
        }

        notificationContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(16))
        }

        val scrollView = ScrollView(this).apply {
            addView(notificationContainer)
        }

        root.addView(header)

        root.addView(
            progressBar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        root.addView(
            emptyText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)

        loadNotifications()
    }

    private fun loadNotifications() {
        notificationListener = FirebaseFirestore.getInstance()
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                progressBar.visibility = View.GONE

                if (error != null) {
                    android.util.Log.e(
                        "NotificationActivity",
                        "Failed to load notifications",
                        error
                    )

                    Toast.makeText(
                        this,
                        "Unable to load notifications",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addSnapshotListener
                }

                notificationContainer.removeAllViews()

                val documents = snapshot?.documents.orEmpty()

                if (documents.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                emptyText.visibility = View.GONE

                documents.forEach { document ->
                    val title =
                        document.getString("title").orEmpty()

                    val description =
                        document.getString("description").orEmpty()

                    val timestamp =
                        document.getTimestamp("timestamp")

                    addNotificationCard(
                        title = title,
                        description = description,
                        timestamp = timestamp
                    )
                }
            }
    }

    private fun addNotificationCard(
        title: String,
        description: String,
        timestamp: Timestamp?
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setBackgroundColor(android.graphics.Color.rgb(248, 248, 248))
            elevation = dp(2).toFloat()
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 17f
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, Typeface.BOLD)
        }

        val descriptionView = TextView(this).apply {
            text = description
            textSize = 14f
            setTextColor(android.graphics.Color.DKGRAY)
            setPadding(0, dp(8), 0, dp(8))
        }

        val timeView = TextView(this).apply {
            text = timestamp?.toDate()?.let { dateFormat.format(it) }
                ?: "Just now"
            textSize = 12f
            setTextColor(android.graphics.Color.GRAY)
        }

        card.addView(titleView)
        card.addView(descriptionView)
        card.addView(timeView)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
        }

        notificationContainer.addView(card, params)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        notificationListener?.remove()
        super.onDestroy()
    }
}
