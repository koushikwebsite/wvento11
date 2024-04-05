package com.wvt.wvento.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.wvt.wvento.data.Repository
import com.wvt.wvento.data.db.LocalEntity
import com.wvt.wvento.models.Event
import com.wvt.wvento.models.Results
import com.wvt.wvento.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
):AndroidViewModel(application) {

    var recyclerViewState: Parcelable? = null

    /** ROOM DATABASE */

    val exploreEvents: LiveData<List<Results>> = repository.local.exploreDatabase().asLiveData()

    val localEvents: LiveData<List<LocalEntity>> = repository.local.localeDatabase().asLiveData()

    fun readCategory(ctg:String): LiveData<List<Results>> {
        return repository.local.readCategory(ctg).asLiveData()
    }

    fun searchEvent(query: String): LiveData<List<Results>> {
        return repository.local.searchEvent(query).asLiveData()
    }

    private fun insertExplore(exploreEntity: List<Results>) =
        viewModelScope.launch {
            repository.local.insertExplore(exploreEntity)
        }

    private fun insertLocal(exploreEntity: LocalEntity) =
        viewModelScope.launch {
            repository.local.insertLocal(exploreEntity)
        }


    /**RETROFIT**/

    private var updateResponse: MutableLiveData<Response<ServerResponse>> = MutableLiveData()

    var postEventResponse : MutableLiveData<Response<ServerResponse>> = MutableLiveData()

    var exploreResponse: MutableLiveData<NetworkResult<Event>> = MutableLiveData()

    var localResponse: MutableLiveData<NetworkResult<Event>> = MutableLiveData()

    fun getLocal(ltn: String) = viewModelScope.launch {
        getLocalSafeCall(ltn)
    }

    fun getExplore() = viewModelScope.launch {
        getExploreSafeCall()
    }

    fun pushEvent(
        video: MultipartBody.Part, image: MultipartBody.Part, id: Int, inputEvt: RequestBody, category: RequestBody, inputLocation: RequestBody,
        inputStrDate: RequestBody, inputEndDate: RequestBody, inputPrice: RequestBody, counter: Int, inputStrTime: RequestBody, inputEndTime: RequestBody,
        inputDesc: RequestBody, usrName: RequestBody, profileUrl: RequestBody
    ) = viewModelScope.launch {

        if(hasInternetConnection()) {
            try {
                val response = repository.remote.postEvents(
                    video, image, id, inputEvt, category, inputLocation, inputStrDate, inputEndDate,
                    inputPrice, counter, inputStrTime, inputEndTime, inputDesc, usrName, profileUrl
                )
                postEventResponse.value = response
            }catch(e: Exception) {
                Log.e("EventViewModel", e.message.toString())
            }
        } else {
            Toast.makeText(getApplication(),"No Internet Connection",Toast.LENGTH_LONG).show()
        }
    }

    suspend fun updateEvent(id: Int) {
        val response = repository.remote.updateEvents(id)
        updateResponse.value = response
    }


    private suspend fun getExploreSafeCall() {
        exploreResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try{

                val response = repository.remote.getExplore()
                exploreResponse.value = handleEventResponse(response)

                val eventDetails = exploreResponse.value!!.data
                if(eventDetails!= null) {
                    offlineCacheExplore(eventDetails)
                }

            }catch (e :Exception) {
                exploreResponse.value = NetworkResult.Error(e.message.toString())
                Log.e("EventViewModel",e.message.toString())
            }
        }
        else {
            exploreResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getLocalSafeCall(ltn: String) {
        localResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try{

                val response = repository.remote.getLocal(ltn)
                localResponse.value = handleEventResponse(response)

                val eventDetails = localResponse.value!!.data
                if(eventDetails!= null) {
                    offlineCacheLocal(eventDetails)
                }

            }catch (e :Exception) {
                localResponse.value = NetworkResult.Error(e.message.toString())
                Log.e("EventViewModel",e.message.toString())
            }
        }
        else {
            localResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun offlineCacheExplore(eventDetails: Event) {
        insertExplore(eventDetails)
    }

    private fun offlineCacheLocal(eventDetails: Event) {
        val local = LocalEntity(eventDetails)
        insertLocal(local)
    }

    private fun handleEventResponse(response: Response<Event>): NetworkResult<Event> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() ==  402 -> {
                NetworkResult.Error("Please Try Again")
            }
            response.isSuccessful -> {
                val events = response.body()
                NetworkResult.Success(events!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}