package com.treasure.hunt.ui.quests

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.treasure.hunt.MainActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.databinding.FragmentQuestsBinding
import com.treasure.hunt.http.UpdateService
import kotlin.random.Random

class QuestsFragment : Fragment() {

    lateinit var booties: List<Treasure>

    lateinit var recyclerViewAdapter: QuestRecyclerViewAdapter
    private var _binding: FragmentQuestsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activity: MainActivity? = this.activity as MainActivity?
        val intent = Intent(activity, UpdateService::class.java)
        activity?.startService(intent)


        _binding = FragmentQuestsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.questsList
        val refreshLayout = binding.swiperefresh
        refreshLayout.setOnRefreshListener {
            updateList()
            refreshLayout.isRefreshing = false
        }
        recyclerView.layoutManager = LinearLayoutManager(activity as Context);
        recyclerViewAdapter = QuestRecyclerViewAdapter(activity as Context, mutableListOf())
        recyclerView.adapter = recyclerViewAdapter
        return root
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
        print("Updated list!")
    }
}