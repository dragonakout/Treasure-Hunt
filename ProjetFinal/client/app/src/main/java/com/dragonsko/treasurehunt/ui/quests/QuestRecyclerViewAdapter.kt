package com.dragonsko.treasurehunt.ui.quests

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dragonsko.treasurehunt.R
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.data.Quest


class QuestRecyclerViewAdapter(val context: Context, var data: MutableList<Quest>, val callback: (quest: Quest) -> (Unit)) : RecyclerView.Adapter<QuestRecyclerViewAdapter.ViewHolder>() {

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
        val quest: Quest = data[position]
        holder.treasureNameView.text = "${quest.name}"
        holder.estimatedValueView.text = "Trésor estimé: ${Utils.formatIntString(quest.estimated_value)} "
        holder.questLengthView.text = "Durée de la quête: 1h" //TODO Calculate distance in meters, show it according to user pref
        holder.questItemRootView.setOnClickListener {
            val bundle = bundleOf(Pair("lattitude", quest.latitude.toFloat()), Pair("longitude", quest.longitude.toFloat()))
            Toast.makeText(context,"Emplacement: ${quest.latitude}, ${quest.longitude}", Toast.LENGTH_SHORT).show()
            holder.itemView.findNavController().navigate(R.id.action_navigation_booty_list_to_navigation_treasure_map, bundle)
        }
        holder.questItemRootView.setOnLongClickListener {
            fun dialogCallback() {
                callback(quest)
            }
            Utils.createConfirmationDialog(context as Activity?, "Abandon de la quête", "Voulez-vous vraiment abandonner la quête?", ::dialogCallback)
            true
        }
        holder.isNewIcon.visibility = if(quest.is_new) View.VISIBLE else View.GONE
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var treasureNameView : TextView = itemView.findViewById(R.id.treasureNameTextView)
        var estimatedValueView : TextView = itemView.findViewById(R.id.estimatedValueTextView)
        var questLengthView : TextView = itemView.findViewById(R.id.questLengthTextView)
        var questItemRootView : View = itemView.findViewById(R.id.quest_item_root)
        var isNewIcon : ImageView = itemView.findViewById(R.id.isNewIcon)
    }
}

