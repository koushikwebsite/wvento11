package com.wvt.wvento.adapter

import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.wvt.wvento.R
import com.wvt.wvento.models.CalEvent
import com.wvt.wvento.models.CtgModel
import com.wvt.wvento.models.Results
import com.wvt.wvento.ui.fragments.CategoryFragmentDirections
import com.wvt.wvento.ui.fragments.ExploreFragmentDirections
import com.wvt.wvento.ui.fragments.GroupFragmentDirections
import com.wvt.wvento.ui.fragments.SearchFragmentDirections
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*


class EventsRowBinding {

    companion object {

        @BindingAdapter("loadImageUrl")
        @JvmStatic
        fun loadImageUrl(imageview: ImageView, imageUrl: String) {
            imageview.load(imageUrl) {
                crossfade(600)
                placeholder(R.drawable.ic_error_placeholder)
                //placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_img)
            }
        }

        @BindingAdapter("loadCategoryUrl")
        @JvmStatic
        fun loadCategoryUrl(imageview: ImageView, imageUrl: Int) {
            imageview.load(imageUrl) {
                crossfade(600)
            }
        }

        @BindingAdapter("loadEventDate")
        @JvmStatic
        fun loadEventDate(textView: TextView, eventDate: String) {
            textView.text = eventDate.substring(0,2)
        }

        @BindingAdapter("loadEventMnh")
        @JvmStatic
        fun loadEventMnh(textView: TextView, eventDate: String) {
            textView.text = eventDate.substring(3,6)
        }

        @BindingAdapter("onEventClickListener")
        @JvmStatic
        fun onEventClickListener(layout: MaterialCardView, result: Results) {
            layout.setOnClickListener{
                try {

                    val action = GroupFragmentDirections.actionGroupFragmentToDetailsActivity(
                        result
                    )
                    layout.findNavController().navigate(action)


                }catch (e: Exception) {
                    Log.d("onEventClickListener",e.toString())
                }
            }
        }

        @BindingAdapter("oneEventStatus")
        @JvmStatic
        fun oneEventStatus(textView: TextView, result: Results) {

            val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

            val current = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                .parse(format.format(Date()))

            val start = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                .parse(result.start_date)

            val end = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                .parse(result.end_date)

            if (current != null) {
                when {
                    current.before(start) -> {
                        textView.text = String.format(textView.context.getString(R.string.evt_open))
                        textView.setTextColor(Color.parseColor("#1db954"))
                    }
                    current.after(end) -> {
                        textView.text = String.format(textView.context.getString(R.string.evt_closed))
                        textView.setTextColor(Color.parseColor("#bd0f15"))
                    }
                    else -> {
                        textView.text = String.format(textView.context.getString(R.string.evt_progress))
                        textView.setTextColor(Color.parseColor("#faa916"))
                    }
                }
            }
        }

        @BindingAdapter("onSearchClickListener")
        @JvmStatic
        fun onSearchClickListener(layout: MaterialCardView, result: Results) {
            layout.setOnClickListener{
                try {
                    val action = ExploreFragmentDirections.actionExploreFragmentToDetailsActivity(
                        result
                    )
                    layout.findNavController().navigate(action)
                }catch (e: Exception) {
                    Log.d("onSearchClickListener",e.toString())
                }
            }
        }

        @BindingAdapter("onExploreClickListener")
        @JvmStatic
        fun onExploreClickListener(layout: MaterialCardView, result: Results) {
            layout.setOnClickListener{
                try {
                    val action = SearchFragmentDirections.actionSearchFragmentToDetailsActivity(
                        result
                    )
                    layout.findNavController().navigate(action)
                }catch (e: Exception) {
                    Log.d("onSearchClickListener",e.toString())
                }
            }
        }

        @BindingAdapter("onCategoryClickListener")
        @JvmStatic
        fun onCategoryClickListener(layout: MaterialCardView, result: Results) {
            layout.setOnClickListener{
                try {
                    val action = CategoryFragmentDirections.actionCategoryFragmentToDetailsActivity(
                        result
                    )
                    layout.findNavController().navigate(action)
                }catch (e: Exception) {
                    Log.d("onComedyClickListener",e.toString())
                }
            }
        }

        @BindingAdapter("onCategoryClickListener")
        @JvmStatic
        fun onCategoryClickListener(layout: ConstraintLayout, result: CtgModel) {
            layout.setOnClickListener{
                try {
                    val action = GroupFragmentDirections.actionGroupFragmentToCategoryFragment(
                        result
                    )
                    layout.findNavController().navigate(action)
                }catch (e: Exception) {
                    Log.d("onCategoryClickListener",e.toString())
                }
            }
        }

        @BindingAdapter("onCalendarLayout")
        @JvmStatic
        fun onCalendarLayout(layout: RelativeLayout, dum: CalEvent) {
            layout.setBackgroundColor(generateColor())
        }

        private fun generateColor(): Int {
            val colors = arrayOf(
                Color.parseColor("#faa916"),
                Color.parseColor("#ef476f"),
                Color.parseColor("#06d6a0"),
                Color.parseColor("#ca6702"),
                Color.parseColor("#ef233c"),
            )
            return colors.random()
        }
    }
}