package com.treasure.hunt.ui.quests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.treasure.hunt.R
import com.treasure.hunt.data.Treasure


class QuestRecyclerViewAdapter(val context: Context, private var data: List<Treasure>) : RecyclerView.Adapter<QuestRecyclerViewAdapter.ViewHolder>() {

    var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return QuestRecyclerViewAdapter.ViewHolder(
            inflater.inflate(R.layout.quest_list_item,
                parent,
                false))
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val treasure: Treasure = data[position]
        holder.treasureNameView.text = "${treasure.size} ${treasure.name} ${treasure.adjective}"
        holder.estimatedValueView.text = "Trésor estimé: ${treasure.estimated_value} "
        holder.questLengthView.text = "Durée de la quête: ${treasure.quest_length}"
        holder.questItemRootView.setOnClickListener {
            Toast.makeText(context,"Emplacement: ${treasure.lattitude}, ${treasure.longitude}", Toast.LENGTH_SHORT).show()
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var treasureNameView : TextView = itemView.findViewById(R.id.treasureNameTextView)
        var estimatedValueView : TextView = itemView.findViewById(R.id.estimatedValueTextView)
        var questLengthView : TextView = itemView.findViewById(R.id.questLengthTextView)
        var questItemRootView : View = itemView.findViewById(R.id.quest_item_root)
    }
}

