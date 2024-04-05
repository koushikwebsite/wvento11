package com.wvt.wvento.util

import android.Manifest
import android.app.Activity
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar

object Permissions {

    fun requestCalenderPermission(fragment: Activity){

        ActivityCompat.requestPermissions(
            fragment,
            arrayOf(Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR),
            1
        )
    }

    fun View.showSnackBar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackBar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackBar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackBar.show()
        }
    }
}