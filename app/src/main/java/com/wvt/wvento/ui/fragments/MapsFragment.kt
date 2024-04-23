package com.wvt.wvento.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentMapsBinding
import java.util.*

class MapsFragment : Fragment() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location?= null
    private var location: String? = null

    private lateinit var locationRequest: LocationRequest

    private lateinit var mMap: GoogleMap

    private lateinit var binding: FragmentMapsBinding

    private var locationPermissionGranted: Boolean = false

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        val latLog = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        drawMarker(latLog)

        mMap.setOnMarkerDragListener(object: OnMarkerDragListener {

            override fun onMarkerDrag(p0: Marker) {}

            override fun onMarkerDragEnd(p0: Marker) {

                p0.snippet = getAddress(p0.position.latitude,p0.position.longitude)
                Log.d("System out", "onMarkerDrag..."+p0.position.latitude+"..."+p0.position.longitude)
            }

            override fun onMarkerDragStart(p0: Marker) {}

        })
    }

    private fun drawMarker(latLog: LatLng) {
        val markerOption = MarkerOptions().position(latLog).title("Your Event is Here")
            .snippet(getAddress(latLog.latitude,latLog.longitude)).draggable(true)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLog))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLog,15f))
        mMap.addMarker(markerOption)
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(latitude,longitude,1)
        location = address?.get(0)?.getAddressLine(0).toString()
        Toast.makeText(requireContext(),location,Toast.LENGTH_LONG).show()
        return location as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =   FragmentMapsBinding.inflate(inflater, container, false)

        binding.getLocation.setOnClickListener {
            val action = MapsFragmentDirections.actionMapsFragmentToCreateEventFragment(location)
            findNavController().navigate(action)
        }

        return binding.root

    }

    private fun requestPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            buildAlertMessageNoGps()
        }
        else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(requireView(),"We need your Location access to upload event location", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) {
                    requestPermission()
                }.show()
            }
            else {
                requestingPermission()
            }
        }
        return locationPermissionGranted
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    override fun onStart() {
        super.onStart()
        requestPermission()
    }

    @SuppressLint("MissingPermission")
    private fun buildAlertMessageNoGps(resolve:Boolean = true) {

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        settingsBuilder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(settingsBuilder.build())

        result.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    exception.startResolutionForResult(requireActivity(), 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("MapsFragment Error", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(requireView(),
                    "Location services must be enabled to get your location", Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    buildAlertMessageNoGps()
                }.show()
            }
        }

        result.addOnCompleteListener {
            if(it.isSuccessful) {
                fusedLocationProviderClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })?.addOnSuccessListener { location ->
                    if (location != null) {
                        this.currentLocation = location
                        val mapFragment =
                            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                        mapFragment?.getMapAsync(callback)
                    }
                }
            }
        }
    }

    private fun requestingPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }
}