package com.aceapps.main.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R
import com.aceapps.main.subs.ProPlans
import com.google.android.material.card.MaterialCardView
import kotlin.text.contains

class VipTipsActivity : BaseActivity() {

    private lateinit var txtTitle : TextView
    private lateinit var btnBack : ImageView

    private lateinit var mcvTwoOdds : MaterialCardView
    private lateinit var mcvFTDraws : MaterialCardView
    private lateinit var mcvFiveOdds : MaterialCardView
    private lateinit var mcvTenOdds : MaterialCardView
    private lateinit var mcvCorrectScore : MaterialCardView
    private lateinit var mcvHTFT : MaterialCardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_tips)
        txtTitle = findViewById(R.id.txtTitle)
        btnBack = findViewById(R.id.btnBack)

        mcvHTFT = findViewById(R.id.mcvHTFT)
        mcvCorrectScore = findViewById(R.id.mcvCorrectScore)
        mcvTenOdds = findViewById(R.id.mcvTenOdds)
        mcvFiveOdds = findViewById(R.id.mcvFiveOdds)
        mcvTwoOdds = findViewById(R.id.mcvTwoOdds)
        mcvFTDraws = findViewById(R.id.mcvFTDraws)


        txtTitle.setOnClickListener {
            finish()
        }


        btnBack.setOnClickListener {
            finish()
        }


        mcvFTDraws.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("ft_draws")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcvFTDraws")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "ft_draws")
                startActivity(intent)

            }
        }

        mcvTwoOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("two_odds")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv2odds")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "two_odds")
                startActivity(intent)
            }

        }

        mcvFiveOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("five_odds")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv5odds")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "five_odds")
                startActivity(intent)
            }
        }

        mcvTenOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("ten_odds")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv10odds")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "ten_odds")
                startActivity(intent)
            }
        }

        mcvCorrectScore.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("correct_score")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcvCorrectVIP")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "correct_score")
                startActivity(intent)
            }
        }

        mcvHTFT.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("htft")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(this, DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv100VIP")
                startActivity(intent)

            }else{
                val intent = Intent(this, SubscriptionActivity::class.java)
                intent.putExtra("product_name", "htft")
                startActivity(intent)
            }
        }




    }


}