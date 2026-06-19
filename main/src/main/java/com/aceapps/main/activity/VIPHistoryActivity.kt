package com.aceapps.main.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R
import com.aceapps.main.adapter.CustomLoader
import com.aceapps.main.adapter.MatchesAdapter
import com.aceapps.main.model.MatchModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class VIPHistoryActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtNoHistory: TextView
    private lateinit var txtTotal: TextView
    private lateinit var adapter: MatchesAdapter

    private val tables = listOf("mcvFTDraws", "mcv2odds", "mcv5odds", "mcv10odds", "mcvCorrectVIP", "mcv100VIP")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_odd_history)

        txtNoHistory = findViewById(R.id.txtNoHistory)
        btnBack = findViewById(R.id.btnBack)
        txtTitle = findViewById(R.id.txtTitle)
        txtTotal = findViewById(R.id.txtTotal)
        recyclerView = findViewById(R.id.rvMatches)

        btnBack.setOnClickListener { finish() }
        txtTitle.setOnClickListener { finish() }

        fetchAllTables()
    }

    private fun fetchAllTables() {
        val db = FirebaseFirestore.getInstance()
        val loader = CustomLoader(this)
        loader.show()

        val yesterdayList = mutableListOf<Pair<String, MatchModel>>()
        var completedCount = 0
        val yesterday = getYesterdayDate()

        for (tableName in tables) {
            db.collection(tableName)
                .get()
                .addOnSuccessListener { result ->



                    for (doc in result) {
                        val match = doc.toObject(MatchModel::class.java)
                        if (match.date == yesterday) {
                            yesterdayList.add(Pair(tableName, match))
                        }
                    }

                    completedCount++
                    if (completedCount == tables.size) {
                        loader.dismiss()
                        showResults(yesterdayList)
                    }
                }
                .addOnFailureListener {
                    completedCount++
                    if (completedCount == tables.size) {
                        loader.dismiss()
                        showResults(yesterdayList)
                    }
                }
        }
    }

    private val tableDisplayNames = mapOf(
        "mcvFTDraws"    to "FT Draws",
        "mcv2odds"      to "2 Odds",
        "mcv5odds"      to "5 Odds",
        "mcv10odds"     to "10 Odds",
        "mcvCorrectVIP" to "Correct Score VIP",
        "mcv100VIP"     to "HTFT VIP"
    )

    private fun showResults(list: MutableList<Pair<String, MatchModel>>) {
        if (list.isEmpty()) {
            txtNoHistory.visibility = View.VISIBLE
            txtTotal.visibility = View.GONE
            return
        }

        txtNoHistory.visibility = View.GONE
        txtTotal.visibility = View.GONE  // hide the global total

        val groupedItems = mutableListOf<Any>()
        val grouped = list.groupBy { it.first }

        for ((tableName, pairs) in grouped) {
            val matches = pairs.map { it.second }

            // Calculate total odds for this group only
            val groupTotal = matches
                .fold(1.0) { acc, m -> acc * (m.odds.toDoubleOrNull() ?: 1.0) }

            val displayName = tableDisplayNames[tableName] ?: tableName

            groupedItems.add(
                "$displayName  |  Total Odds: %.2f".format(groupTotal)
            )
            groupedItems.addAll(matches)
        }

        adapter = MatchesAdapter(groupedItems)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    private fun getYesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(cal.time)
    }
}