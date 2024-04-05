package com.wvt.wvento.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentCreateEventBinding
import com.wvt.wvento.util.NetworkListener
import com.wvt.wvento.util.Permissions.showSnackBar
import com.wvt.wvento.util.ProgressButton
import com.wvt.wvento.util.RealPathUtil
import com.wvt.wvento.viewModel.EventViewModel
import com.wvt.wvento.viewModel.ServerResponse
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private var _binding : FragmentCreateEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var layout: View

    private lateinit var viewModel: EventViewModel
    private lateinit var networkListener: NetworkListener

    private var selectedImagePath: File? = null
    private var selectedVideoPath: File? = null

    private var hadPermission: Boolean = false
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var progressButton: ProgressButton
    private val args by navArgs<CreateEventFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)

        layout = binding.createLayout

        viewModel = ViewModelProvider(this)[EventViewModel::class.java]

        binding.Toolbar.setTitleTextColor(Color.WHITE)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.cardBtn.setOnClickListener{

            val inputEvt = binding.inputEvt.text.toString()
            val inputLocation = binding.inputLocation.text.toString()
            val inputStartDate = binding.inputStartDate.text.toString()
            val inputEndDate = binding.inputEndDate.text.toString()
            val inputStartTime = binding.inputStartTime.text.toString()
            val inputEndTime = binding.inputEndTime.text.toString()
            val inputPrice = binding.inputPrice.text.toString()
            val inputDesc = binding.inputDescription.text.toString()
            val category = binding.autoCompleteCtg.text.toString()

            progressButton = ProgressButton(it)

            if(!TextUtils.isEmpty(inputEvt) && !TextUtils.isEmpty(inputLocation)
                && inputLocation!= "click here to select location"
                && !TextUtils.isEmpty(inputPrice) && !TextUtils.isEmpty(inputEndDate)
                && !TextUtils.isEmpty(inputStartTime) && !TextUtils.isEmpty(inputEndTime)
                && !TextUtils.isEmpty(inputDesc) && !TextUtils.isEmpty(inputStartDate)
            ) {

                lifecycleScope.launch {
                    checkUploadCondition(inputEvt,inputLocation,inputStartDate,inputEndDate, inputPrice, inputStartTime, inputEndTime, inputDesc, category)
                }
            }
            else {
                Toast.makeText(requireContext(),"Please Enter all fields", Toast.LENGTH_LONG).show()
            }
        }

        binding.inputStartTime.setOnClickListener {
            val picker1 = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Event Time")
                .build()

            picker1.show(requireActivity().supportFragmentManager, "Event_TIME_PICKER")
            startEventTime(picker1)
        }

        binding.inputEndTime.setOnClickListener {
            val picker2 = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Event Time")
                .build()

            picker2.show(requireActivity().supportFragmentManager, "Event_TIME_PICKER")
            endEventTime(picker2)
        }

        binding.Toolbar.setNavigationOnClickListener {
            showExitDialog()
        }

        binding.inputStartDate.setOnClickListener {
            startEventData()
        }

        binding.inputEndDate.setOnClickListener {
            endEventDate()
        }

        binding.posterContainer.setOnClickListener {
            if(requestPermission()) {
                imageChooser()
            }
        }

        binding.videoContainer.setOnClickListener {
            if(requestPermission()) {
                videoChooser()
            }
        }

        binding.inputLocation.setOnClickListener {
            findNavController().navigate(R.id.action_createEventFragment_to_mapsFragment)
        }

        return binding.root
    }

    private suspend fun checkUploadCondition(
        inputEvt: String, inputLocation: String, inputStartDate: String, inputEndDate: String,
        inputPrice: String, inputStartTime: String, inputEndTime: String, inputDesc: String, category: String
    ) {
        networkListener = NetworkListener()
        networkListener.checkNetworkAvailability(requireContext()).collect {
            if (it) {
                uploadEvent(selectedVideoPath,inputEvt,inputLocation,inputStartDate,inputEndDate, inputPrice, inputStartTime, inputEndTime, inputDesc, category, mAuth.currentUser?.displayName.toString(), mAuth.currentUser?.photoUrl.toString())
            } else {
                Toast.makeText(requireActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Do you want to close it")
            .setPositiveButton("Yes") { _, _ ->
                activity?.finish()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputLocation.text = args.location
    }

    //REQUESTING STORAGE PROCESS
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted:Boolean ->
        hadPermission = isGranted
    }

    private fun requestPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            hadPermission = true
        }
        else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                layout.showSnackBar(requireView(),
                    getString(R.string.storage_permission_req),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
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
        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    //IMAGE SELECTING FUNCTION
    private fun imageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun videoChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        resultLauncher1.launch(intent)
    }

    //REQUESTING IMAGE SELECT PROCESS
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data?.data!=null) {

                binding.poster.visibility = View.VISIBLE

                binding.poster.setImageURI(data.data)
                //selectedImagePath = File(data.data!!.path.toString()).absoluteFile
                val image = RealPathUtil.getRealPath(requireContext(),data.data)
                selectedImagePath = File(Uri.parse(image).path.toString())
                binding.uploadMedia.visibility = View.GONE
            }
            else {
                binding.uploadMedia.visibility = View.VISIBLE
            }
        }
    }

    private var resultLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if(data?.data!=null) {

                binding.montage.visibility = View.VISIBLE

                binding.montage.setVideoURI(data.data)

                val video = RealPathUtil.getRealPath(requireContext(),data.data)
                selectedVideoPath = File(Uri.parse(video).path.toString())
                binding.uploadVideo.visibility = View.GONE
            }
            else {
                binding.uploadVideo.visibility = View.VISIBLE
            }
        }
    }

    private fun endEventTime(picker: MaterialTimePicker) {

        picker.addOnPositiveButtonClickListener {
            val hour:String = if(picker.hour<10) {
                "0${picker.hour}"
            } else {
                picker.hour.toString()
            }

            val minute:String = if(picker.minute<10){
                "0${picker.minute}"
            }else {
                picker.minute.toString()
            }
            binding.inputEndTime.text = String.format(getString(R.string.evt_time_place),hour,minute)
        }
    }

    private fun startEventTime(picker: MaterialTimePicker) {

        picker.addOnPositiveButtonClickListener {
            val hour:String = if(picker.hour<10) {
                "0${picker.hour}"
            } else {
                picker.hour.toString()
            }

            val minute:String = if(picker.minute<10){
                "0${picker.minute}"
            }else {
                picker.minute.toString()
            }
            binding.inputStartTime.text = String.format(getString(R.string.evt_time_place),hour,minute)
        }
    }

    private fun uploadEvent(
        selectedVideoPath: File?, inputEvt: String, inputLocation: String, inputStartDate: String, inputEndDate: String, inputPrice: String,
        inputStartTime: String, inputEndTime: String, inputDesc: String, category: String, usrName: String, profileUrl: String
    ) {

        progressButton.buttonActivated(requireContext())

        val videoName = inputEvt + mAuth.uid
        val imageName = inputEvt + "Image" + mAuth.uid

        val requestBody: RequestBody = selectedVideoPath!!
            .asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val fileToUpload = MultipartBody.Part.createFormData(
            "filename",
            videoName, requestBody
        )

        val requestBody1: RequestBody = selectedImagePath!!
            .asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imageToUpload = MultipartBody.Part.createFormData(
            "filename1",
            imageName, requestBody1
        )

        val name = inputEvt.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val location = inputLocation.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val strDate = inputStartDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val endDate = inputEndDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val evtPrice = inputPrice.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val startTime = inputStartTime.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val endTime = inputEndTime.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val description = inputDesc.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ctg = category.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val usrNm = usrName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val usrProfile = profileUrl.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rank = (1..50).random()

        viewModel.pushEvent( fileToUpload, imageToUpload, 0, name, ctg, location, strDate,
            endDate, evtPrice, rank, startTime, endTime, description, usrNm, usrProfile
        )

        viewModel.postEventResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Log.d("Upload", response.body().toString())
                Log.d("Upload", response.code().toString())
                Log.d("Upload", response.message().toString())

                val uploadTask: ServerResponse? = response.body()

                when (uploadTask?.status) {
                    1 -> {
                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                        progressButton.buttonFinished(requireContext())
                        navigate()
                    }
                    0 -> {
                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                        progressButton.buttonDeActivated(requireContext())
                    }
                }

            } else {
                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_LONG)
                    .show()
                progressButton.buttonDeActivated(requireContext())
            }
        }
    }

    private fun navigate() {
        //findNavController().popBackStack()
        if (Build.VERSION.SDK_INT >= 33) {
            activity?.onBackInvokedDispatcher?.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {

            }
        }
    }

    private fun startEventData() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Event date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.MaterialCalendarTheme)
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "Event_Date_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            binding.inputStartDate.text = convertLongToDate(it)
        }
    }

    private fun endEventDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Event date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.MaterialCalendarTheme)
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "Event_Date_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            binding.inputEndDate.text = convertLongToDate(it)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time:Long):String {

        val date = Date(time)
        val format = SimpleDateFormat("dd MMM yyyy")
        return format.format(date)

    }

    override fun onStart() {
        super.onStart()
        showSoftKeyboard(binding.inputEvt)
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter1 = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.selectCategory,
            R.layout.dropdown_item
        )
        binding.autoCompleteCtg.setAdapter(adapter1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}