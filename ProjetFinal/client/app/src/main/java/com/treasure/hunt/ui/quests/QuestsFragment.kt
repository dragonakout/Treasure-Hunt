package com.treasure.hunt.ui.quests

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.treasure.hunt.MainActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.treasure.hunt.Utils
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.databinding.FragmentQuestsBinding
import com.treasure.hunt.http.UpdateService
import kotlin.random.Random

class QuestsFragment : Fragment() {

    lateinit var booties: List<Treasure>

    lateinit var recyclerViewAdapter: QuestRecyclerViewAdapter
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
        recyclerViewAdapter = QuestRecyclerViewAdapter(activity as Context, (activity as MainActivity).treasures, ::onLongClickCallback)
        recyclerView.adapter = recyclerViewAdapter
        return root
    }
    // TODO: Fix the item duplication
    private fun onLongClickCallback(treasure: Treasure) {
        val treasures = (activity as MainActivity).treasures
        recyclerViewAdapter.data = treasures
        treasures.remove(treasure)
        recyclerViewAdapter.notifyDataSetChanged()
        postRemovedTreasure(treasure)
    }

    private fun postRemovedTreasure(treasure: Treasure) {
        val userId = Utils.getUserId(activity)!!
        val params = mapOf(
            Pair("treasure_id",treasure.id.toString()),
            Pair("user_id", userId),
        )
        val url = Utils.BASE_URL + "/" + userId + "/dropquest"
        Utils.post(url, params)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateList() {
        (activity as MainActivity).updateData()
    }
}