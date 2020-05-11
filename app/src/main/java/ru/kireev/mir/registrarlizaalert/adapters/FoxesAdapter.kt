package ru.kireev.mir.registrarlizaalert.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.foxes_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Fox

class FoxesAdapter(private val context: Context) : RecyclerView.Adapter<FoxesAdapter.FoxesViewHolder>() {
    var foxes: List<Fox> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoxesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.foxes_item, parent, false)
        return FoxesViewHolder(view)
    }

    override fun getItemCount() = foxes.size

    override fun onBindViewHolder(holder: FoxesViewHolder, position: Int) {
        val fox = foxes[position]
        holder.tvFoxesItemNumberTitle.text = fox.numberOfFox
        val elderTemplate = context.resources.getString(R.string.elder_template)
        holder.tvFoxesItemElder.text = String.format(
                elderTemplate,
                fox.elderOfFox.name,
                fox.elderOfFox.surname,
                fox.elderOfFox.callSign)
    }

    inner class FoxesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoxesItemNumberTitle: TextView = itemView.tvFoxesItemNumberTitle
        val tvFoxesItemElder: TextView = itemView.tvFoxesItemElder
    }
}