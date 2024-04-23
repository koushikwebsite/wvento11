package com.wvt.wvento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wvt.wvento.databinding.SingleCheckBinding
import com.wvt.wvento.models.SelectModel


class SelectionAdapter (private var hotels: List<SelectModel>): RecyclerView.Adapter<SelectionAdapter.MyViewHolder>() {

    private var selectedPosition:Int = -1

    class MyViewHolder(val binding: SingleCheckBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            SingleCheckBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return hotels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.ltnNm.text = hotels[position].location

        holder.binding.ltnImg.load(hotels[position].img)

        holder.binding.ltnCtn.isChecked = position == selectedPosition

        holder.binding.ltnCtn.setOnClickListener {

            selectedPosition = holder.adapterPosition
            notifyItemRangeChanged(0, hotels.size)

        }
    }

    fun getSelected(): SelectModel? {
        return if (selectedPosition != -1) {
            hotels[selectedPosition]
        } else null
    }
}