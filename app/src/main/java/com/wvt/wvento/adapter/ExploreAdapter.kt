package com.wvt.wvento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wvt.wvento.databinding.ItemExploreBinding
import com.wvt.wvento.models.Results
import com.wvt.wvento.util.EventDiffUtil

class ExploreAdapter: RecyclerView.Adapter<ExploreAdapter.MyViewHolder>() {

    private var events = emptyList<Results>()

    class MyViewHolder(private val binding: ItemExploreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Results){
            binding.event = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemExploreBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentResult = events[position]
        holder.bind(currentResult)
    }

    fun setData(newData: List<Results>) {
        val eventDifUtil = EventDiffUtil(events, newData)
        val diffUtilResult = DiffUtil.calculateDiff(eventDifUtil)
        events = newData
        diffUtilResult.dispatchUpdatesTo(this)

    }
}