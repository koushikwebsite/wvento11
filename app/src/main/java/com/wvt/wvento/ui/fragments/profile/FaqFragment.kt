package com.wvt.wvento.ui.fragments.profile

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wvt.wvento.adapter.FaqAdapter
import com.wvt.wvento.databinding.FragmentFaqBinding
import com.wvt.wvento.models.FaqModel

class FaqFragment : Fragment() {

    private lateinit var binding: FragmentFaqBinding

    private val ltnList = generateList()

    private val myAdapter by lazy { FaqAdapter(ltnList) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFaqBinding.inflate(inflater, container, false)

        binding.Toolbar.setTitleTextColor(Color.WHITE)

        binding.Toolbar.setNavigationOnClickListener {
            navigate()
        }

        setUpRecyclerview()

        return binding.root
    }

    private fun setUpRecyclerview() {
        binding.rvFaq.adapter = myAdapter
        binding.rvFaq.layoutManager = LinearLayoutManager(activity)
        binding.rvFaq.setHasFixedSize(true)
    }

    private fun generateList(): ArrayList<FaqModel> {

        val list = ArrayList<FaqModel>()
        list.add(
            FaqModel(
                "What is Wvento?",
                "Wvento is an free app which helps you find events nearby with specific interests. It also allows users to conduct or create an event and spread a word.",
                false
            )
        )
        list.add(FaqModel("How to use Wvento?", "Awesome 2", false))
        list.add(
            FaqModel(
                "How we are different from others",
                "Wvento specifically focuses on helping you discover ongoing events around you which matches your interests and make creating events seamlessly fast and efficient.",
                false
            )
        )
        list.add(
            FaqModel(
                "Can i create my own events?",
                "Yes, you can create your own events by clicking the create icon at the bottom right.",
                false
            )
        )
        list.add(
            FaqModel(
                "is Wvento free to use?",
                "Yes, Wvento is a free to use app.",
                false
            )
        )
        list.add(
            FaqModel(
                "How do i contact support Team?",
                "You can contact the support team by going into account and clicking on help and support.",
                false
            )
        )
        list.add(
            FaqModel(
                "How to Report a violation",
                "You can report us your query by filling contact us in Profile -> Help and Support -> Contact us",
                false
            )
        )
        list.add(
            FaqModel(
                "Report impersonation",
                "We don't allow accounts that mislead or deceive others. We may update, transfer, or permanently suspend accounts that do so. If someone is pretending to be you, contact us",
                false
            )
        )
        return list
    }

    private fun navigate() {
        findNavController().popBackStack()
    }
}