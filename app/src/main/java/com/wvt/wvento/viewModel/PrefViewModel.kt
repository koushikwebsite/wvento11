package com.wvt.wvento.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wvt.wvento.data.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrefViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
): AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    val readLocationStore = dataStoreRepository.readUserLocationType.asLiveData()
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun savedToDataStore(location: String,position: Int) = viewModelScope.launch {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveUserLocationType(
                location, position
            )
        }
        Log.d("PrefViewModel", "entered into savedToDataStore")
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

}