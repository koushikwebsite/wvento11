package com.wvt.wvento.ui.fragments

import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wvt.wvento.R
import com.wvt.wvento.adapter.CalenderAdapter
import com.wvt.wvento.databinding.FragmentCalenderBinding
import com.wvt.wvento.models.CalEvent
import com.wvt.wvento.util.Permissions.showSnackBar
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class CalenderFragment : Fragment() {

    private lateinit var layout: View
    private lateinit var binding: FragmentCalenderBinding
    private val myAdapter by lazy { CalenderAdapter() }
    private var hadPermission: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCalenderBinding.inflate(inflater, container, false)

        layout = binding.calLayout

        setUpRecyclerview()

        lifecycleScope.launchWhenStarted {
            requestPermission()
        }

        binding.calender.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            val calendar = Calendar.getInstance()
            calendar[year, month] = dayOfMonth
            val sDate = sdf.format(calendar.time)
            setInfo(readCalendarEvent(sDate))
        }

        return binding.root
    }

    private fun requestPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_CALENDAR)
            == PackageManager.PERMISSION_GRANTED) {
            hadPermission = true

            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            val current = sdf.format(Date())
            setInfo(readCalendarEvent(current))
        }
        else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CALENDAR)) {

                layout.showSnackBar(requireView(), getString(R.string.cal_permission_req),
                    Snackbar.LENGTH_INDEFINITE, getString(R.string.ok))
                {
                    requestingPermission()
                }
            }
            else {
               requestingPermission()
            }
        }
        return hadPermission
    }

    private fun requestingPermission() {
        permissionLauncher.launch(android.Manifest.permission.READ_CALENDAR)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hadPermission = isGranted
    }

    private fun setUpRecyclerview() {
        binding.rvCalender.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
        }
    }

    private fun readCalendarEvent(s: String): ArrayList<CalEvent> {

        val dummyEvents = ArrayList<CalEvent>()

        val cursor: Cursor? = requireContext().contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {

                if (s == getDateTime(it.getLong(3))) {

                    val calendarId = it.getString(0)
                    val title = it.getString(1)
                    val description = it.getString(2)
                    val dtStart = it.getString(3)
                    val dtEnd = it.getString(4)

                    val dummyEvent = CalEvent(
                        calendarId, title, description, dtStart, dtEnd
                    )
                    dummyEvents.add(dummyEvent)
                }
            }

            cursor.close()
        }
        return dummyEvents
    }

    private fun setInfo(calEvents: ArrayList<CalEvent>) {
        if (calEvents.isEmpty()) {

            myAdapter.setData(calEvents)
            binding.emptyCal.visibility = View.VISIBLE

        } else {

            myAdapter.setData(calEvents)
            binding.emptyCal.visibility = View.GONE
        }

        binding.eventCount.text = String.format(getString(R.string.cal_evt_count),myAdapter.itemCount)
    }

    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            val netDate = Date(s)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}