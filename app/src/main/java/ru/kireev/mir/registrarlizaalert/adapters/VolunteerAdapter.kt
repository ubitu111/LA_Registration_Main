package ru.kireev.mir.registrarlizaalert.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.volunteer_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Volunteer

class VolunteerAdapter : RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    var volunteers: List<Volunteer> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var onVolunteerLongClickListener: OnVolunteerLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.volunteer_item, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun getItemCount() = volunteers.size

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]
        with(holder) {
            textViewPosition.text = (position + 1).toString()
            textViewName.text = volunteer.name
            textViewSurname.text = volunteer.surname
            textViewCallSign.text = volunteer.callSign
            textViewPhoneNumber.text = volunteer.phoneNumber
            if (volunteer.carMark.isNotEmpty()) {
                linearLayoutFirstGroupInfoCar.visibility = View.VISIBLE
                linearLayoutSecondGroupInfoCar.visibility = View.VISIBLE
                textViewCarMark.text = volunteer.carMark
                textViewCarModel.text = volunteer.carModel
                textViewCarColor.text = volunteer.carColor
                textViewCarRegistrationNumber.text = volunteer.carRegistrationNumber
            } else {
                linearLayoutSecondGroupInfoCar.visibility = View.GONE
                linearLayoutFirstGroupInfoCar.visibility = View.GONE
            }
            itemView.setOnLongClickListener {
                onVolunteerLongClickListener?.onLongClick(volunteer)
                return@setOnLongClickListener true
            }
        }

    }

    inner class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.textViewName
        val textViewSurname: TextView = itemView.textViewSurname
        val textViewCallSign: TextView = itemView.textViewCallSign
        val textViewPhoneNumber: TextView = itemView.textViewPhoneNumber
        val textViewPosition: TextView = itemView.textViewPosition
        val textViewCarMark: TextView = itemView.textViewCarMark
        val textViewCarModel: TextView = itemView.textViewCarModel
        val textViewCarRegistrationNumber: TextView = itemView.textViewCarRegistrationNumber
        val textViewCarColor: TextView = itemView.textViewCarColor
        val linearLayoutFirstGroupInfoCar: LinearLayout = itemView.linearLayoutFirstGroupInfoCar
        val linearLayoutSecondGroupInfoCar: LinearLayout = itemView.linearLayoutSecondGroupInfoCar
    }

    interface OnVolunteerLongClickListener {
        fun onLongClick(volunteer: Volunteer)
    }
}