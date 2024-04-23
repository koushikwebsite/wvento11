package com.wvt.wvento.data.api

import com.wvt.wvento.models.Event
import com.wvt.wvento.viewModel.ServerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface EventApi {

    @FormUrlEncoded
    @POST("fetch.php")
    suspend fun getLocal(
        @Field("event_location") location: String,
    ): Response<Event>

    @GET("explore.php")
    suspend fun getExplore(): Response<Event>

    @FormUrlEncoded
    @POST("update.php")
    suspend fun updateEvent(
        @Field("event_id") id: Int,
    ): Response<ServerResponse>

    @Multipart
    @POST("insert.php")
    suspend fun postEvent(
        @Part file: MultipartBody.Part?,
        @Part image: MultipartBody.Part?,
        @Part("event_id") id: Int,
        @Part("event_name") title: RequestBody,
        @Part("category") category: RequestBody,
        @Part("event_location") location: RequestBody,
        @Part("start_date") start_date: RequestBody,
        @Part("end_date") end_date: RequestBody,
        @Part("event_price") price: RequestBody,
        @Part("counter") counter: Int,
        @Part("event_str_time") event_str_time: RequestBody,
        @Part("event_end_time") event_end_time: RequestBody,
        @Part("event_desc") event_desc: RequestBody,
        @Part("event_user") event_user: RequestBody,
        @Part("event_userUrl") event_userUrl: RequestBody,
    ): Response<ServerResponse>

}
