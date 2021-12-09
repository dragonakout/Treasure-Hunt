package com.treasure.hunt.ui.quests

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.treasure.hunt.MainActivity
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.databinding.FragmentQuestsBinding

class QuestsFragment : Fragment() {


    private var _binding: FragmentQuestsBinding? = null
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {


        _binding = FragmentQuestsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.questsList
        val refreshLayout = binding.swiperefresh
        refreshLayout.setOnRefreshListener {
            updateList()
            refreshLayout.isRefreshing = false
        }
        recyclerView.layoutManager = LinearLayoutManager(activity as Context);
        val adapter = QuestRecyclerViewAdapter(activity as Context, (activity as MainActivity).treasures, ::onLongClickCallback)
        recyclerView.adapter = adapter
        return root
    }

    private fun onLongClickCallback(treasure: Treasure) {
        val treasures = (activity as MainActivity).treasures
        val index = treasures.indexOf(treasure)
        treasures.remove(treasure)
        recyclerView.adapter?.notifyItemRemoved(index)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        print("Updated list!") // TODO: Add fetch treasures from the server here
    }
}