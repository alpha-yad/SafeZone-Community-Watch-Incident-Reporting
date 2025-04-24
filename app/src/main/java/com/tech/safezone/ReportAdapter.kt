package com.tech.safezone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase


class ReportAdapter(private val reportList: List<IncidentReport>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val database = FirebaseDatabase.getInstance().getReference()

    class ReportViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.reportTitle)
        val description: TextView = itemView.findViewById(R.id.reportDescription)
        val time: TextView = itemView.findViewById(R.id.reportTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.title.text = report.title
        holder.description.text = report.description
        holder.time.text = report.timestamp
    }

    override fun getItemCount(): Int {
        return reportList.size
    }
}
