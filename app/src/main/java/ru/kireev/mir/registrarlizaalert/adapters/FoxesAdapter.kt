package ru.kireev.mir.registrarlizaalert.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.foxes_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Fox
import ru.kireev.mir.registrarlizaalert.listeners.OnDeleteFoxClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnFoxClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class FoxesAdapter(private val context: Context) : RecyclerView.Adapter<FoxesAdapter.FoxesViewHolder>() {
    var foxes: List<Fox> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onVolunteerPhoneNumberClickListener: OnVolunteerPhoneNumberClickListener? = null
    var onDeleteFoxClickListener: OnDeleteFoxClickListener? = null
    var onFoxClickListener: OnFoxClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoxesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.foxes_item, parent, false)
        return FoxesViewHolder(view)
    }

    override fun getItemCount() = foxes.size

    override fun onBindViewHolder(holder: FoxesViewHolder, position: Int) {
        val fox = foxes[position]
        val numberOfFox = context.resources.getString(R.string.foxes_item_number_of_fox)
        with(holder) {
            tvFoxesItemNumberTitle.text = String.format(numberOfFox, fox.numberOfFox)
            tvDate.text = fox.dateOfCreation
            val elderTemplate = context.resources.getString(R.string.elder_template)
            tvFoxesItemElder.text = String.format(
                    elderTemplate,
                    fox.elderOfFox.name,
                    fox.elderOfFox.surname,
                    fox.elderOfFox.callSign)
            tvFoxesItemElderPhone.text = fox.elderOfFox.phoneNumber
            ivDeleteFox.setOnClickListener {
                onDeleteFoxClickListener?.onDeleteFoxClick(fox)
            }
            tvFoxesItemElderPhone.setOnClickListener {
                onVolunteerPhoneNumberClickListener?.onVolunteerPhoneNumberClick(tvFoxesItemElderPhone.text.toString())
            }
            cardViewFox.setOnClickListener {
                onFoxClickListener?.onFoxClick(holder.adapterPosition)
            }
        }

    }

    inner class FoxesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoxesItemNumberTitle: TextView = itemView.tvFoxesItemNumberTitle
        val tvFoxesItemElder: TextView = itemView.tvFoxesItemElder
        val tvFoxesItemElderPhone: TextView = itemView.tvFoxesItemElderPhone
        val cardViewFox: CardView = itemView.cardViewFox
        val ivDeleteFox: ImageView = itemView.ivDeleteFox
        val tvDate: TextView = itemView.tvDate
    }
}