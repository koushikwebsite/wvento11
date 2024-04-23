package com.wvt.wvento.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.navigation.ActivityNavigator
import com.wvt.wvento.R
import com.wvt.wvento.databinding.ActivityNewEventBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val window: Window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }
}