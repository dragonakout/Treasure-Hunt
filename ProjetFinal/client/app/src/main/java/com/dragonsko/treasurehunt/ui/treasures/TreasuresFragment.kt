package com.dragonsko.treasurehunt.ui.treasures

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonsko.treasurehunt.MainActivity
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.databinding.FragmentTreasuresBinding

class TreasuresFragment : Fragment() {


    private var _binding: FragmentTreasuresBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTreasuresBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val treasureValueTextView: TextView = binding.bootyTotalValue
        var totaltreasure = (activity as MainActivity).collectedTreasures.sumOf { t -> t.actual_value }

        treasureValueTextView.text = "Butin total : ${Utils.formatIntString(totaltreasure)} "

        val recyclerView = binding.bootyList
        recyclerView.layoutManager = LinearLayoutManager(activity as Context);
        val adapter = TreasuresRecyclerViewAdapter(activity as Context, (activity as MainActivity).collectedTreasures )
        recyclerView.adapter = adapter
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}