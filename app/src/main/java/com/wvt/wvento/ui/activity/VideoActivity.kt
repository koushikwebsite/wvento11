package com.wvt.wvento.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.wvt.wvento.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private var mCurrent = 0
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaController = MediaController(this)
        mediaController.setMediaPlayer(binding.eventVideo)
        binding.eventVideo.setMediaController(mediaController)

        val b = intent.extras
        val videoUrl = b!!.getString("video")

//        val args by navArgs<VideoActivityArgs>()

        initializePlayer(
            //Uri.parse("android.resource://" + packageName + "/" + R.raw.video1)

            Uri.parse(videoUrl)
        )

        binding.eventVideo.setOnCompletionListener {
            binding.eventVideo.seekTo(0)
            finish()
        }

    }

    private fun initializePlayer(videoUri: Uri) {

        binding.progressVideo.visibility = View.VISIBLE
        binding.eventVideo.setVideoURI(videoUri)

        binding.eventVideo.setOnPreparedListener {
            if(mCurrent>0)
                binding.eventVideo.seekTo(mCurrent)
            else
                binding.eventVideo.seekTo(1)

            binding.progressVideo.visibility = View.GONE
            binding.eventVideo.start()
        }

    }

    private fun releasePlayer() {
        binding.eventVideo.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

}