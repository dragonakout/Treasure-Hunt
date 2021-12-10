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
        updateNoQuestUI((activity as MainActivity).treasures.size <= 0)
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
        updateNoQuestUI(treasures.size <= 0)
        postRemovedTreasure(treasure)
    }

    private fun postRemovedTreasure(treasure: Treasure) {
        val userId = Utils.getUserId(activity)!!
        val params = mapOf(
            Pair("treasure_id",treasure.id.toString()),
            Pair("user_id", userId),
        )
        val url = Utils.BASE_URL + "/dropquest"
        Utils.post(url, params)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateList() {
        (activity as MainActivity).updateData()
    }

    fun updateNoQuestUI(isListEmpty : Boolean) {
        if(isListEmpty) {
            binding.noQuestsPlaceholderExplanationTextview.visibility = View.VISIBLE
            binding.noQuestsPlaceholderImageView.visibility = View.VISIBLE
            binding.noQuestsPlaceholderNoQuestTextview.visibility = View.VISIBLE
            binding.questsList.visibility = View.GONE
        } else {
            binding.noQuestsPlaceholderExplanationTextview.visibility = View.GONE
            binding.noQuestsPlaceholderImageView.visibility = View.GONE
            binding.noQuestsPlaceholderNoQuestTextview.visibility = View.GONE
            binding.questsList.visibility = View.VISIBLE
        }
    }
}