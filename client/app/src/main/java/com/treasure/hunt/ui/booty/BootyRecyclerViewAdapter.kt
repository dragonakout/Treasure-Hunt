package com.treasure.hunt.ui.booty

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.treasure.hunt.R
import com.treasure.hunt.Utils
import com.treasure.hunt.data.Treasure

class BootyRecyclerViewAdapter(context: Context, private var treasures: MutableList<Treasure>) : RecyclerView.Adapter<BootyRecyclerViewAdapter.ViewHolder>() {

    var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.booty_list_item,
                parent,
                false))
    }

    override fun getItemCount(): Int {
        return treasures.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val treasure: Treasure = treasures[position]
        holder.treasureNameView.text = "${treasure.size} ${treasure.name} ${treasure.adjective}"
        holder.estimatedValueView.text = "Valeur: ${Utils.formatIntString(treasure.actual_value.toInt())} "
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var treasureNameView : TextView = itemView.findViewById(R.id.treasureNameTextView)
        var estimatedValueView : TextView = itemView.findViewById(R.id.estimatedValueTextView)
    }
}
