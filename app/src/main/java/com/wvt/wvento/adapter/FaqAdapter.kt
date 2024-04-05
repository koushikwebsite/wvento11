package com.wvt.wvento.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wvt.wvento.R
import com.wvt.wvento.databinding.ItemFaqBinding
import com.wvt.wvento.models.FaqModel

class FaqAdapter(private var faqList: List<FaqModel>) : RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemFaqBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.sectionName.text = faqList[position].name
        holder.binding.description.text = faqList[position].description

        holder.binding.faqContainer.setOnClickListener {
            faqList[position].expand = !faqList[position].expand
            notifyItemChanged(position)
        }

        if(faqList[position].expand) {
            holder.binding.expandedView.visibility = View.VISIBLE
            holder.binding.imageView4.setImageResource(R.drawable.ic_arrow_drop_up)
            holder.binding.faqContainer.setBackgroundResource(R.drawable.ic_border_active)

        } else {
            holder.binding.expandedView.visibility = View.GONE
            holder.binding.imageView4.setImageResource(R.drawable.ic_arrow_drop_down)
            holder.binding.faqContainer.setBackgroundResource(R.drawable.ic_border)

        }

    }
}