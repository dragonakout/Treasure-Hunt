package com.dragonsko.treasurehunt.ui.treasures

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dragonsko.treasurehunt.R
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.data.Treasure

class TreasuresRecyclerViewAdapter(context: Context, private var treasures: MutableList<Treasure>) : RecyclerView.Adapter<TreasuresRecyclerViewAdapter.ViewHolder>() {

    var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.treasure_list_item,
                parent,
                false))
    }

    override fun getItemCount(): Int {
        return treasures.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quest: Treasure = treasures[position]
        holder.treasureNameView.text = "${quest.name}"
        holder.estimatedValueView.text = "Valeur: ${Utils.formatIntString(quest.actual_value)} "
        holder.collectDateTextView.text = "Collecté le ${quest.collected_timestamp}"
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var treasureNameView : TextView = itemView.findViewById(R.id.treasureNameTextView)
        var estimatedValueView : TextView = itemView.findViewById(R.id.estimatedValueTextView)
        var collectDateTextView : TextView = itemView.findViewById(R.id.collectDateTextView)
    }
}
