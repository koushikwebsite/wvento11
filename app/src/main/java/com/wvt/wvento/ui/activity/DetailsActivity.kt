package com.wvt.wvento.ui.activity

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import androidx.navigation.navArgs
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.wvt.wvento.R
import com.wvt.wvento.databinding.ActivityDetailsBinding
import com.wvt.wvento.models.Results
import com.wvt.wvento.util.Permissions
import com.wvt.wvento.viewModel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: EventViewModel

    private val args by navArgs<DetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[EventViewModel::class.java]

        getData()

        val window: Window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

        binding.playVideo.setOnClickListener {
            val i = Intent(this, VideoActivity::class.java)
            i.putExtra("video", args.data1?.event_video)
            startActivity(i)
        }

        binding.Toolbar.setOnClickListener {
            finish()
        }

        binding.Toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.detailShare -> {
                    lifecycleScope.launch {
                        createShareIntent()
                    }
                }
            }
            true
        }

        binding.evtReminderBtn.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            {
                lifecycleScope.launch {
                    addEvent()
                }

            }else {

                Permissions.requestCalenderPermission(this)
            }
        }
    }

    private suspend fun createShareIntent() {

        try {

            val strShareMessage = "${args.data1?.title}\n${args.data1?.event_desc}\n\n " +
                    "Let me recommend you this application\n"+ "Explore Upcoming Events at your location Download it Now !" +
                    "https://play.google.com/store/apps/details?id="+this.packageName

            convertBitmap(args.data1!!.event_poster)

            val file = File(this.externalCacheDir!!.absolutePath+"/shared_events/"+args.data1!!.title+".png")
            val imageFullPath: String = file.absolutePath

            val i = Intent(Intent.ACTION_SEND)
            i.type = "image/*"
            i.putExtra(Intent.EXTRA_TEXT, strShareMessage)
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFullPath))
            startActivity(Intent.createChooser(i, "Select App to Share this Event"))

        } catch (e: Exception) {

            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun convertBitmap(eventPoster: String) {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(eventPoster)
            .build()
        val result = (loading.execute(request) as SuccessResult).drawable

        saveImage((result as BitmapDrawable).bitmap)
    }

    private fun saveImage(finalBitmap: Bitmap) {
        val root: String = externalCacheDir!!.absolutePath
        val myDir = File("$root/shared_events")
        myDir.mkdirs()
        val fName = args.data1!!.title+".png"
        val file = File(myDir, fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun addEvent() {
        val formatter: DateFormat = SimpleDateFormat("dd MMM yyyy , hh : mm", Locale.ENGLISH)
        val lnsTime: Long
        val lneTime: Long

        val dateObject1: Date = formatter.parse(args.data1?.start_date +" , "+ args.data1?.start_time) as Date
        lnsTime = dateObject1.time

        val dateObject2: Date = formatter.parse(args.data1?.end_date +" , "+ args.data1?.end_time) as Date
        lneTime = dateObject2.time

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, lnsTime)
            put(CalendarContract.Events.DTEND, lneTime)
            put(CalendarContract.Events.TITLE, args.data1?.title)
            put(CalendarContract.Events.DESCRIPTION, args.data1?.event_desc)
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().timeZone.id)
        }

        val uri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val mEventId = uri!!.lastPathSegment!!.toLong()
        setReminder(contentResolver,mEventId)
    }

    private suspend fun setReminder(contentResolver: ContentResolver?, mEventId: Long) {

        val values = ContentValues().apply {
            put(CalendarContract.Reminders.MINUTES, 1440)
            put(CalendarContract.Reminders.EVENT_ID, mEventId)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver?.insert(CalendarContract.Reminders.CONTENT_URI, values)

        val values1 = ContentValues().apply {
            put(CalendarContract.Reminders.MINUTES, 90)
            put(CalendarContract.Reminders.EVENT_ID, mEventId)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver?.insert(CalendarContract.Reminders.CONTENT_URI, values1)

        Toast.makeText(this, "Event Reminder added to your calender", Toast.LENGTH_SHORT).show()

        viewModel.updateEvent(args.data1?.id!!)

    }


    private fun getData()  = this.lifecycleScope.launchWhenCreated {

        if (args.data1!=null) {

            val myBundle: Results = args.data1!!

            binding.apply {

                detailPosterImg.load(myBundle.event_poster) {
                    crossfade(600)
                    placeholder(R.drawable.ic_error_placeholder)
                }

                detailEventDesc.text = myBundle.event_desc
                conductedUsr.text = myBundle.event_user
                evtDate.text = String.format(
                    getString(R.string.evt_duration),
                    myBundle.start_date,
                    myBundle.end_date
                )
                evtPrice.text = myBundle.event_price
                evtTime.text = String.format(
                    getString(R.string.evt_duration),
                    myBundle.start_time,
                    myBundle.end_time
                )
                evtCategory.text = myBundle.category
                detailEventTitle.text = myBundle.title
            }

            makeTextViewResizable(binding.detailEventDesc, 3, "View More", true)

            val spanLoc =
                SpannableString(String.format(getString(R.string.click_here), myBundle.location))
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val map = "http://maps.google.co.in/maps?q=${myBundle.location}"
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(map))
                    startActivity(i)

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FF03DAC5")
                }
            }
            spanLoc.setSpan(
                clickableSpan,
                spanLoc.length - 11,
                spanLoc.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            binding.evtLocation.text = spanLoc
            binding.evtLocation.movementMethod = LinkMovementMethod.getInstance()

        }
    }

    private fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                    addClickablePartTextViewResizable(
                        SpannableString(tv.text.toString()), tv, expandText, viewMore
                    ), BufferType.SPANNABLE
                )
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        sText: String, viewMore: Boolean
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(sText)) {
            ssb.setSpan(object : ClickableSpan() {

                override fun onClick(widget: View) {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false)
                    } else {
                        makeTextViewResizable(tv, 3, "View More", true)
                    }
                }
            }, str.indexOf(sText), str.indexOf(sText) + sText.length, 0)
        }
        return ssb
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}