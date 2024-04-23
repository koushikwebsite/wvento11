package com.wvt.wvento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wvt.wvento.databinding.ItemCalendarBinding
import com.wvt.wvento.models.CalEvent
import com.wvt.wvento.util.CalendarDiffUtil

class CalenderAdapter: RecyclerView.Adapter<CalenderAdapter.MyViewHolder>() {

    private var dummy = emptyList<CalEvent>()

    class MyViewHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: CalEvent){
            binding.calendar = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCalendarBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dummy.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentResult = dummy[position]
        holder.bind(currentResult)
    }

    fun setData(newData: List<CalEvent>) {
        val eventDifUtil = CalendarDiffUtil(dummy, newData)
        val diffUtilResult = DiffUtil.calculateDiff(eventDifUtil)
        dummy = newData
        diffUtilResult.dispatchUpdatesTo(this)

    }
}