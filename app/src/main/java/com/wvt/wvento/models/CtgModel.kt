package com.wvt.wvento.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CtgModel (
    val ctgName: String,
    val name:String,
    val imageResourceId: Int,
    val ctgTag: String
): Parcelable