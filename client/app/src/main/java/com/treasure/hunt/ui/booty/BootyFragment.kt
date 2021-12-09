package com.treasure.hunt.ui.booty

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.treasure.hunt.MainActivity
import com.treasure.hunt.Utils
import com.treasure.hunt.databinding.FragmentBootyBinding

class BootyFragment : Fragment() {


    private var _binding: FragmentBootyBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBootyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bootyValueTextView: TextView = binding.bootyTotalValue
        var totalBooty = Utils.readStringFromSharedPrefs("total_booty",activity)
        if (totalBooty.isNullOrBlank()) totalBooty = "0"

        bootyValueTextView.text = "Butin total : $totalBooty "

        val recyclerView = binding.bootyList
        recyclerView.layoutManager = LinearLayoutManager(activity as Context);
        val adapter = BootyRecyclerViewAdapter(activity as Context, (activity as MainActivity).collectedTreasures )
        recyclerView.adapter = adapter
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}