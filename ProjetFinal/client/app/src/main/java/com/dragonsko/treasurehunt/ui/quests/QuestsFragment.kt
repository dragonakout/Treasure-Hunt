package com.dragonsko.treasurehunt.ui.quests

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonsko.treasurehunt.MainActivity
import androidx.recyclerview.widget.RecyclerView
import com.dragonsko.treasurehunt.MainApplication
import com.dragonsko.treasurehunt.R
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.data.Quest
import com.dragonsko.treasurehunt.databinding.FragmentQuestsBinding
import kotlinx.coroutines.launch

class QuestsFragment : Fragment() {

    lateinit var recyclerViewAdapter: QuestRecyclerViewAdapter
    private var _binding: FragmentQuestsBinding? = null
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuestsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        recyclerView = binding!!.questsList
        val refreshLayout = binding!!.swiperefresh
        refreshLayout.setOnRefreshListener {
            updateList()
            refreshLayout.isRefreshing = false
        }
        activity?.runOnUiThread {
            updateNoQuestUI((activity as MainActivity).quests.size <= 0)
        }
        recyclerView.layoutManager = LinearLayoutManager(activity as Context);
        recyclerViewAdapter = QuestRecyclerViewAdapter(activity as Context, (activity as MainActivity).quests, ::onLongClickCallback)
        recyclerView.adapter = recyclerViewAdapter
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.navigation_settings -> {
                findNavController().navigate(R.id.action_navigation_quest_list_to_navigation_settings)
                true
            }
            R.id.navigation_about -> {
                findNavController().navigate(R.id.action_navigation_quest_list_to_navigation_about)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun onLongClickCallback(quest: Quest) {
        val treasures = (activity as MainActivity).quests
        recyclerViewAdapter.data = treasures
        treasures.remove(quest)
        recyclerViewAdapter.notifyDataSetChanged()
        activity?.runOnUiThread {
            updateNoQuestUI(treasures.size <= 0)
        }
        (activity?.applicationContext as MainApplication).applicationScope.launch {
            Utils.DBdelete(quest, activity?.applicationContext)
        }
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
            binding?.noQuestsPlaceholderExplanationTextview?.visibility = View.VISIBLE
            binding?.noQuestsPlaceholderImageView?.visibility = View.VISIBLE
            binding?.noQuestsPlaceholderNoQuestTextview?.visibility = View.VISIBLE
            binding?.questsList?.visibility = View.GONE
        } else {
            binding?.noQuestsPlaceholderExplanationTextview?.visibility = View.GONE
            binding?.noQuestsPlaceholderImageView?.visibility = View.GONE
            binding?.noQuestsPlaceholderNoQuestTextview?.visibility = View.GONE
            binding?.questsList?.visibility = View.VISIBLE
        }
    }
}