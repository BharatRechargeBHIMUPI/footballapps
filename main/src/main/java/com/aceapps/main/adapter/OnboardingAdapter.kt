package com.aceapps.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R

class OnboardingAdapter(
    private val slides: List<OnboardingSlide>
) : RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder>() {

    class SlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgSlide)
        val title: TextView = view.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val slide = slides[position]
        holder.img.setImageResource(slide.imageRes)
        holder.title.text = slide.title
    }

    override fun getItemCount() = slides.size
}

data class OnboardingSlide(
    val imageRes: Int,
    val title: String
)