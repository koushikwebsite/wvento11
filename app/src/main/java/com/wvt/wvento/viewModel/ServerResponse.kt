package com.wvt.wvento.viewModel

import com.google.gson.annotations.SerializedName

data class ServerResponse(
    @SerializedName("status")
    var status: Int,
    @SerializedName("message")
    var message: String
)
