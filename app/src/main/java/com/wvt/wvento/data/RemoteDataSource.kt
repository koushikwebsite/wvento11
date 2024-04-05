package com.wvt.wvento.data

import com.wvt.wvento.data.api.EventApi
import com.wvt.wvento.models.Event
import com.wvt.wvento.viewModel.ServerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(
    private val eventApi: EventApi
) {

    suspend fun getExplore(): Response<Event> {
        return eventApi.getExplore()
    }

    suspend fun getLocal(ltn: String): Response<Event> {
        return eventApi.getLocal(ltn)
    }

    suspend fun updateEvents(id: Int) :Response<ServerResponse> {
        return eventApi.updateEvent(id)
    }

    suspend fun postEvents(
        video: MultipartBody.Part,
        image: MultipartBody.Part,
        id: Int,
        title: RequestBody,
        category: RequestBody,
        location: RequestBody,
        start_date: RequestBody,
        end_date: RequestBody,
        price: RequestBody,
        counter: Int,
        event_str_time: RequestBody,
        event_end_time: RequestBody,
        event_desc: RequestBody,
        usrName: RequestBody,
        profileUrl: RequestBody

    ) : Response<ServerResponse> {
        return eventApi.postEvent(video,image,id,title,category,location,start_date,end_date,price,counter,event_str_time,event_end_time,event_desc, usrName, profileUrl)
    }


}