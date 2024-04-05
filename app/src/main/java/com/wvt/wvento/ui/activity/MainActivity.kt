package com.wvt.wvento.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.wvt.wvento.R
import com.wvt.wvento.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Evento)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenStarted {
            if (onBoardingFinished()) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

        val window: Window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

    }

    private fun onBoardingFinished(): Boolean{
        val sharedPref = getSharedPreferences("ScreenComplete", MODE_PRIVATE)
        return sharedPref.getBoolean("IntroFinish", false)
    }

}
