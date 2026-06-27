package com.aceapps.main.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R
import com.aceapps.main.adapter.CustomLoader
import com.aceapps.main.adapter.MatchesAdapter
import com.aceapps.main.model.MatchModel
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class DailyMatchesActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: FrameLayout
    private lateinit var txtTitle: TextView
    private lateinit var txtNote: LinearLayout
    private lateinit var txtNoHistory: LinearLayout
    private lateinit var txtTotal: TextView
    private lateinit var tabToday: TextView
    private lateinit var tabYesterday: TextView
    private lateinit var adapter: MatchesAdapter

    private val todayList = mutableListOf<MatchModel>()
    private val yesterdayList = mutableListOf<MatchModel>()
    private var currentTab = "TODAY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_free_score)

        txtNote = findViewById(R.id.txtNote)
        txtNoHistory = findViewById(R.id.txtNoHistory)
        btnBack = findViewById(R.id.btnBack)
        txtTitle = findViewById(R.id.txtTitle)
        txtTotal = findViewById(R.id.txtTotal)
        recyclerView = findViewById(R.id.rvMatches)
        tabToday = findViewById(R.id.tabToday)
        tabYesterday = findViewById(R.id.tabYesterday)

        val tableName = intent.getStringExtra("value") ?: "matches"

        txtTitle.text = when (tableName) {
            "mcvFTDraws"     -> "FT Draws VIP"
            "mcv2odds"       -> "Two Odds VIP"
            "mcv5odds"       -> "Five Odds VIP"
            "mcv10odds"      -> "Ten Odds VIP"
            "mcvCorrectVIP"  -> "Correct Score VIP"
            "mcv100VIP"      -> "HTFT VIP"
            else             -> "Daily Free Match"
        }

        when (tableName) {
            "mcvFTDraws"     -> txtNote.visibility = View.VISIBLE
            "mcvCorrectVIP"  -> txtNote.visibility = View.VISIBLE
            "mcv100VIP"      -> txtNote.visibility = View.VISIBLE
            else             -> txtNote.visibility = View.GONE
        }

        fetchMatches(tableName)

        tabToday.setOnClickListener {
            tabYesterday.setTextColor(getColor(R.color.tab_text_unselect_color))
            tabToday.setTextColor(getColor(R.color.tab_text_select_color))

            switchTab("TODAY") }
        tabYesterday.setOnClickListener {
            tabYesterday.setTextColor(getColor(R.color.tab_text_select_color))
            tabToday.setTextColor(getColor(R.color.tab_text_unselect_color))

            switchTab("YESTERDAY") }

        btnBack.setOnClickListener { finish() }
        txtTitle.setOnClickListener { finish() }
    }


    fun rateUsDialog() {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->

                }
            } else {
            }
        }
    }

    private fun switchTab(tab: String) {
        currentTab = tab

        // Update tab highlight
        tabToday.setBackgroundResource(
            if (tab == "TODAY") R.drawable.bg_tab_active else R.drawable.bg_tab_unactive
        )
        tabYesterday.setBackgroundResource(
            if (tab == "YESTERDAY") R.drawable.bg_tab_active else R.drawable.bg_tab_unactive
        )
        val displayList: List<MatchModel> = if (tab == "TODAY") todayList else yesterdayList
        adapter = MatchesAdapter(displayList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (displayList.isEmpty()){
            txtNoHistory.visibility = View.VISIBLE
        }else{
            txtNoHistory.visibility = View.GONE
        }

        // Update total odds

        try {

            var total = displayList
                .fold(1.0) { acc, value -> acc * value.odds.toDouble() }

            if (displayList.isEmpty()) total = 0.0

            txtTotal.text = "Total Odds: %.2f".format(total)

        }catch (e: Exception){}

    }

    private fun fetchMatches(tableName: String) {
        val db = FirebaseFirestore.getInstance()
        val loader = CustomLoader(this)
        loader.show()

        db.collection(tableName)
            .get()
            .addOnSuccessListener { result ->
                todayList.clear()
                yesterdayList.clear()

                for (doc in result) {

                    val match = doc.toObject(MatchModel::class.java)
                    Log.d("<<<>>>", match.country)

                    when (match.date) {  // compare directly, no formatDate needed
                        getTodayDate()     -> todayList.add(match)
                        getYesterdayDate() -> yesterdayList.add(match)
                    }
                }

                switchTab("TODAY")
                loader.dismiss()

                lifecycleScope.launch {
                    delay(5000)
                    rateUsDialog()
                }
            }
            .addOnFailureListener {
                txtNoHistory.visibility = View.VISIBLE
                loader.dismiss()
            }
    }

    private fun getYesterdayDate(): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(cal.time)
    }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())
    }
}