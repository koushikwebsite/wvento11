package com.wvt.wvento.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.wvt.wvento.R

class ProgressButton(
    view: View
) {

    private val progressUpload: ProgressBar = view.findViewById(R.id.progress_upload)
    private val evtBtnTxt: TextView = view.findViewById(R.id.evt_btn_txt)
    private val consLayout:RelativeLayout = view.findViewById(R.id.cons_layout)

    fun buttonActivated(ct:Context) {
        progressUpload.visibility = View.VISIBLE
        evtBtnTxt.text = ct.getString(R.string.uploading)
    }

    fun buttonFinished(ct: Context) {
        consLayout.setBackgroundColor(Color.parseColor("#1FC335"))
        progressUpload.visibility = View.GONE
        evtBtnTxt.text = ct.getString(R.string.uploaded)
    }

    fun buttonDeActivated(ct:Context) {
        consLayout.setBackgroundColor(Color.parseColor("0D47A1"))
        progressUpload.visibility = View.GONE
        evtBtnTxt.text = ct.getString(R.string.upload_your_event)
    }
}