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
    private lateinit var questsViewModel: QuestsViewModel
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

    fun createTreasures() : List<Treasure>{
        val list : MutableList<Treasure> = mutableListOf()
        for(i in 0..9) {
            val booty_size = POSSIBLE_BOOTY_SIZE[Random.Default.nextInt(0,POSSIBLE_BOOTY_SIZE.size)]
            val booty_name = POSSIBLE_BOOTY_NAME[Random.Default.nextInt(0,POSSIBLE_BOOTY_NAME.size)]
            val booty_adj = POSSIBLE_BOOTY_ADJECTIVE[Random.Default.nextInt(0,POSSIBLE_BOOTY_ADJECTIVE.size)]
            val booty_value = Random.Default.nextInt(5,20) * 1000
            val booty_value_mul = 1 + (Random.Default.nextInt(1,10) / 10.0)
            val treasure = Treasure(i,booty_size,booty_adj,booty_name,0.0f,0.0f, booty_value, booty_value * booty_value_mul,"1h")
            list.add(treasure)
        }
        return list
    }

    companion object {
        val POSSIBLE_BOOTY_SIZE = listOf("Gigantesque", "Immense", "Gros", "Abondant", "Grand", "Maigre", "Petit", "Massif")
        val POSSIBLE_BOOTY_NAME = listOf("trésor", "héritage", "magot", "butin")
        val POSSIBLE_BOOTY_ADJECTIVE = listOf("maudit", "mythique", "fantastique", "légendaire", "épique", "glorieux", "oublié", "prisé", "sanglant", "royal", "scintillant", "inimaginable")
    }
}