package com.tech.safezone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(private val serviceList: List<ServiceRequest>) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceType: TextView = itemView.findViewById(R.id.serviceTypeText)
        val description: TextView = itemView.findViewById(R.id.descriptionText)
        val status: TextView = itemView.findViewById(R.id.statusText)
        val timestamp: TextView = itemView.findViewById(R.id.timestampText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = serviceList[position]
        holder.serviceType.text = service.serviceType
        holder.description.text = service.description
        holder.status.text = service.status
        holder.timestamp.text = service.timestamp
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }
} 