package com.wvt.wvento.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wvt.wvento.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        binding.Toolbar.setTitleTextColor(Color.WHITE)

        binding.Toolbar.setNavigationOnClickListener {
            navigate()
        }

        return binding.root
    }

    private fun navigate() {
        findNavController().popBackStack()
    }

}