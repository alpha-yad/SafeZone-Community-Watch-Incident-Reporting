package com.tech.safezone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RewardsAdapter(private val rewards: ArrayList<Reward>) :
    RecyclerView.Adapter<RewardsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val descriptionText: TextView = view.findViewById(R.id.descriptionText)
        val pointsText: TextView = view.findViewById(R.id.pointsText)
        val redeemButton: Button = view.findViewById(R.id.redeemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reward = rewards[position]
        holder.titleText.text = reward.title
        holder.descriptionText.text = reward.description
        holder.pointsText.text = "${reward.pointsRequired} points"

        holder.redeemButton.setOnClickListener {
            // TODO: Implement reward redemption logic
        }
    }

    override fun getItemCount() = rewards.size
} 