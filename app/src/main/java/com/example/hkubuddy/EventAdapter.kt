package com.example.hkubuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private val events: MutableList<Task>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false) // Use custom layout
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val task = events[position]
        holder.taskName.text = task.name
        holder.taskDescription.text = task.description
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Task>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
    }
}