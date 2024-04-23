package com.wvt.wvento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wvt.wvento.databinding.ItemSearchBinding
import com.wvt.wvento.models.CtgModel

class SearchCtgAdapter(private var category: List<CtgModel>): RecyclerView.Adapter<SearchCtgAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: CtgModel){
            binding.category = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentResult = category[position]
        holder.bind(currentResult)
    }
}