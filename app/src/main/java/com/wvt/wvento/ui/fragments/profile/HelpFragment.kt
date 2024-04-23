package com.wvt.wvento.ui.fragments.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentHelpBinding
import com.wvt.wvento.util.Constants


class HelpFragment : Fragment() {

    private lateinit var binding: FragmentHelpBinding
    private var builder = CustomTabsIntent.Builder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater, container, false)

        binding.Toolbar.setTitleTextColor(Color.WHITE)

        binding.Toolbar.setNavigationOnClickListener {
            navigate()
        }

        binding.instagram.setOnClickListener {
            socialMedia("com.instagram.android",Constants.INSTA1,Constants.INSTA1)
        }

        binding.twitter.setOnClickListener {
            socialMedia("com.twitter.android",Constants.TWITTER,Constants.TWITTER)
        }

        binding.website.setOnClickListener {
            val customTabsIntent : CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.BASE_URL))
        }

        binding.youtube.setOnClickListener {
            socialMedia("com.android.youtube",Constants.YOUTUBE, Constants.YOUTUBE)
        }

        binding.about.setOnClickListener {
            val customTabsIntent : CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.ABOUT_US))
        }

        binding.contact.setOnClickListener {
            val mailto = "mailto:wventocompany@gmail.com"
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(mailto)
            try {
                startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Error to open email app", Toast.LENGTH_SHORT).show()
            }
        }

        binding.faq.setOnClickListener {
            findNavController().navigate(R.id.action_helpFragment_to_faqFragment)
        }

        binding.terms.setOnClickListener {
            val customTabsIntent : CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.TERMS))
        }

        binding.policy.setOnClickListener {
            val customTabsIntent : CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.POLICY))
        }

        return binding.root
    }

    private fun socialMedia(pack: String, link1: String, link2: String) {
        var intent: Intent?
        try {
            requireActivity().packageManager.getPackageInfo(pack, 0)
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link1)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(link2))
        }
        this.startActivity(intent)
    }

    private fun navigate() {
        activity?.finish()
    }

}