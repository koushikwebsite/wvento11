package com.wvt.wvento.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.wvt.wvento.R
import com.wvt.wvento.databinding.ActivityHomeBinding
import com.wvt.wvento.viewModel.PrefViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var manager: ReviewManager
    private var reviewInfo: ReviewInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initReviews()

        prefViewModel = ViewModelProvider(this)[PrefViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_home_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        prefViewModel.readLocationStore.observe(this) { value ->
            Firebase.messaging.subscribeToTopic(value.selectedLocationType)
        }
    }

    private fun initReviews() {
        manager = ReviewManagerFactory.create(this)
        manager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful) {
                reviewInfo = request.result
                askForReview()
            }
        }
    }

    private fun askForReview() {
        reviewInfo?.let {
            manager.launchReviewFlow(this, it).addOnFailureListener {
                Toast.makeText(applicationContext, "In App Rating failed", Toast.LENGTH_LONG).show()

            }.addOnCompleteListener {

                Toast.makeText(applicationContext, "In App Rating complete", Toast.LENGTH_LONG).show()
            }
        }
    }

}