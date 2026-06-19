package com.aceapps.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R
import com.aceapps.main.model.MatchModel
import com.google.android.material.card.MaterialCardView

class MatchesAdapter(private val items: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_MATCH = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TYPE_HEADER else TYPE_MATCH
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_match, parent, false)
            MatchViewHolder(view)
        }


    }


    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String)
        } else if (holder is MatchViewHolder) {
            holder.bind(items[position] as MatchModel)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeader: TextView = itemView.findViewById(R.id.tvHeader)

        fun bind(title: String) {
            tvHeader.text = title
        }
    }

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvCountry: TextView = itemView.findViewById(R.id.tvCountry)
        private val tvLeagueTime: TextView = itemView.findViewById(R.id.tvLeagueTime)
        private val tvHomeTeam: TextView = itemView.findViewById(R.id.tvHomeTeam)
        private val tvAwayTeam: TextView = itemView.findViewById(R.id.tvAwayTeam)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val tvPick: TextView = itemView.findViewById(R.id.tvPick)
        private val ivHomeLogo1: TextView = itemView.findViewById(R.id.ivHomeLogo1)
        private val ivHomeLogo2: TextView = itemView.findViewById(R.id.ivHomeLogo2)
        private val tvOdds: TextView = itemView.findViewById(R.id.tvOdds)
        private val mcvScore: MaterialCardView = itemView.findViewById(R.id.mcvScore)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(model: MatchModel) {

            tvLeagueTime.text = "${model.time}"
            tvHomeTeam.text = model.team1
            tvAwayTeam.text = model.team2
            tvPick.text = "Pick: ${model.pick}"
            tvOdds.text = model.odds
            if (model.score.isEmpty()){
                mcvScore.visibility = View.GONE
            }else{
                mcvScore.visibility = View.VISIBLE
                tvScore.text = model.score
            }

            ivHomeLogo1.text = model.team1Emoji.split(" ").firstOrNull() ?: "🏳️"
            ivHomeLogo2.text = model.team2Emoji.split(" ").firstOrNull() ?: "🏳️"

            tvDate.text = model.date
            tvCountry.text = model.country

            when (model.status) {

                "Pending" -> {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "PENDING"
                    tvStatus.setTextColor(Color.parseColor("#000000"))
                    tvStatus.setBackgroundColor(Color.parseColor("#E6A817"))
                }

                "Win" -> {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "WIN"
                    tvStatus.setTextColor(Color.parseColor("#000000"))
                    tvStatus.setBackgroundColor(Color.parseColor("#2ECC71"))
                }

                "Lose" -> {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "LOSS"
                    tvStatus.setBackgroundColor(Color.parseColor("#E74C3C"))
                }

                "Live" -> {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "LIVE"
                    tvStatus.setBackgroundColor(Color.parseColor("#FF0000"))

                    val blink = android.view.animation.AnimationUtils
                        .loadAnimation(itemView.context, R.anim.blink)

                    tvStatus.startAnimation(blink)
                }
            }
        }
    }
}