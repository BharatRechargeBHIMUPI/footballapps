package com.aceapps.main.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.aceapps.main.R
import com.aceapps.main.activity.DailyMatchesActivity
import com.aceapps.main.activity.SubscriptionActivity
import com.aceapps.main.activity.VIPHistoryActivity
import com.aceapps.main.subs.ProPlans
import com.google.android.material.card.MaterialCardView
import kotlin.text.contains

class HomeFragment : Fragment() {

    private lateinit var view : View

    private lateinit var llDailyMatch : LinearLayout
    private lateinit var vipHistoryCard : LinearLayout
    private lateinit var lPro : View
    private lateinit var ivPremium : ImageView

    private lateinit var mcvTwoOdds : MaterialCardView
    private lateinit var mcvFTDraws : MaterialCardView
    private lateinit var mcvFiveOdds : MaterialCardView
    private lateinit var mcvTenOdds : MaterialCardView
    private lateinit var mcvCorrectScore : MaterialCardView
    private lateinit var mcvHTFT : MaterialCardView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_home, container, false)
        init()
        return view
    }

    private fun init(){
        llDailyMatch = view.findViewById(R.id.llDailyMatch)
        vipHistoryCard = view.findViewById(R.id.vipHistoryCard)
        ivPremium = view.findViewById(R.id.ivPremium)
        lPro = view.findViewById(R.id.lPro)
        mcvHTFT = view.findViewById(R.id.mcvHTFT)
        mcvCorrectScore = view.findViewById(R.id.mcvCorrectScore)
        mcvTenOdds = view.findViewById(R.id.mcvTenOdds)
        mcvFiveOdds = view.findViewById(R.id.mcvFiveOdds)
        mcvTwoOdds = view.findViewById(R.id.mcvTwoOdds)
        mcvFTDraws = view.findViewById(R.id.mcvFTDraws)



        lPro.setOnClickListener {
            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
            intent.putExtra("product_name", "mega_subscription")
            startActivity(intent)
        }

        ivPremium.setOnClickListener {
            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
            intent.putExtra("product_name", "mega_subscription")
            startActivity(intent)
        }

        llDailyMatch.setOnClickListener {
            val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
            intent.putExtra("value", "mcvFreeGames")
            startActivity(intent)
        }

        vipHistoryCard.setOnClickListener {
            val intent = Intent(requireActivity(), VIPHistoryActivity::class.java)
            startActivity(intent)
        }

        mcvFTDraws.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("ft_draws") || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcvFTDraws")
                startActivity(intent)

            }else{
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                intent.putExtra("product_name", "ft_draws")
                startActivity(intent)

            }
        }

        mcvTwoOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("two_odds")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv2odds")
                startActivity(intent)

            }else{
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                intent.putExtra("product_name", "two_odds")
                startActivity(intent)
            }

        }

        mcvFiveOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("five_odds")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv5odds")
                startActivity(intent)

            }else{
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                intent.putExtra("product_name", "five_odds")
                startActivity(intent)
            }
        }

        mcvTenOdds.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("ten_odds")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv10odds")
                startActivity(intent)

            }else{
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                intent.putExtra("product_name", "ten_odds")
                startActivity(intent)
            }
        }

        mcvCorrectScore.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("correct_score")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcvCorrectVIP")
                startActivity(intent)

            }else{
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                intent.putExtra("product_name", "correct_score")
                startActivity(intent)
            }
        }

        mcvHTFT.setOnClickListener {
            var openPage = false
            for (i in ProPlans.activeSubscription.indices){
                if (ProPlans.activeSubscription[i].contains("htft")  || ProPlans.activeSubscription[i].contains("mega_subscription")){
                    openPage = true
                }
            }
            if (openPage){
                val intent = Intent(requireActivity(), DailyMatchesActivity::class.java)
                intent.putExtra("value", "mcv100VIP")
                startActivity(intent)

            }else{
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                intent.putExtra("product_name", "htft")
                startActivity(intent)
            }
        }


    }
}