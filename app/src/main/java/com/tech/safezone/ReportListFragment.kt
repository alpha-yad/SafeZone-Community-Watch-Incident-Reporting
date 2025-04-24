package com.tech.safezone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReportListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportList: ArrayList<IncidentReport>
    private lateinit var adapter: ReportAdapter
    private val database = FirebaseDatabase.getInstance().getReference("reports")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_report_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportList = ArrayList()
        adapter = ReportAdapter(reportList)
        recyclerView.adapter = adapter

        // Fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reportList.clear()
                for (dataSnapshot in snapshot.children) {
                    val report = dataSnapshot.getValue(IncidentReport::class.java)
                    if (report != null) {
                        reportList.add(report)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load reports", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}