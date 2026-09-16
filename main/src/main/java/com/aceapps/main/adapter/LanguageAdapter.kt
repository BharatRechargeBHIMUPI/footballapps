package com.aceapps.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aceapps.main.R

class LanguageAdapter(
    private val languages: MutableList<Language>,
    private val onLanguageSelected: (Int) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    inner class LanguageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val container: View =
            itemView.findViewById(R.id.languageContainer)

        // Emoji flag is now a TextView
        val flag: TextView =
            itemView.findViewById(R.id.tvFlag)

        val name: TextView =
            itemView.findViewById(R.id.tvLanguage)

        val radio: ImageView =
            itemView.findViewById(R.id.ivRadio)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_language,
                parent,
                false
            )

        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int
    ) {

        val language = languages[position]

        // Emoji flag
        holder.flag.text = language.flag

        // Language name
        holder.name.text = language.name

        // Selected / unselected UI
        if (language.selected) {

            holder.container.setBackgroundResource(
                R.drawable.language_selected
            )

            holder.radio.setImageResource(
                R.drawable.radio_selected
            )

        } else {

            holder.container.setBackgroundResource(
                R.drawable.language_unselected
            )

            holder.radio.setImageResource(
                R.drawable.radio_unselected
            )
        }

        holder.itemView.setOnClickListener {
            onLanguageSelected(position)
        }
    }

    override fun getItemCount(): Int {
        return languages.size
    }
}