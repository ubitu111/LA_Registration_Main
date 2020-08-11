package ru.kireev.mir.registrarlizaalert.adapters

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.volunteer_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener
import ru.kireev.mir.registrarlizaalert.util.TimePickerFragment
import java.util.*

class VolunteerAdapter(private val context: Context, private val viewModel: VolunteersViewModel) : RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    var volunteers: List<Volunteer> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun filterVolunteers(query: String?, fullList: List<Volunteer>): List<Volunteer> {
        val results = mutableListOf<Volunteer>()
        val queryString = query?.toLowerCase(Locale.getDefault())?.trim()
        queryString?.let {
            for (volunteer in fullList) {
                if (volunteer.name.toLowerCase(Locale.getDefault()).contains(it)
                        || volunteer.surname.toLowerCase(Locale.getDefault()).contains(it)
                        || volunteer.callSign.toLowerCase(Locale.getDefault()).contains(it)
                        || volunteer.carRegistrationNumber.toLowerCase(Locale.getDefault()).contains(it)
                        || volunteer.status.toLowerCase(Locale.getDefault()).contains(it)) {
                    results.add(volunteer)
                }
            }
        }
        return results
    }

    var onVolunteerPhoneNumberClickListener: OnVolunteerPhoneNumberClickListener? = null
    var onChangeVolunteerStatusListener: OnChangeVolunteerStatusListener? = null
    var onVolunteerChangeTimeToSearchListener: OnVolunteerChangeTimeToSearchListener? = null

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
            tvVolunteerStatus.text = volunteer.status
            when (volunteer.status) {
                context.getString(R.string.volunteer_status_active) -> {
                    tvVolunteerStatus.setTextColor(context.resources.getColor(R.color.green))
                }
                context.getString(R.string.volunteer_status_left) -> {
                    tvVolunteerStatus.setTextColor(context.resources.getColor(R.color.red))
                }
            }
            tvVolunteerTimeForSearch.text = volunteer.timeForSearch
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

            tvOptionsVolunteerItem.setOnClickListener {
                showPopup(tvOptionsVolunteerItem, volunteer)
            }
            textViewPhoneNumber.setOnClickListener {
                onVolunteerPhoneNumberClickListener?.onVolunteerPhoneNumberClick(textViewPhoneNumber.text.toString())
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
        val tvVolunteerStatus: TextView = itemView.tvVolunteerStatus
        val tvOptionsVolunteerItem: TextView = itemView.tvOptionsVolunteerItem
        val tvVolunteerTimeForSearch: TextView = itemView.tvVolunteerTimeForSearch
    }

    private fun showPopup(textView: TextView, volunteer: Volunteer) {
        val popup = PopupMenu(context, textView)
        popup.inflate(R.menu.volunter_item_options_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.options_menu_change_status -> {
                    changeStatus(volunteer)
                }
                R.id.options_menu_set_time -> {
                    setTime(volunteer)
                }
            }
            return@setOnMenuItemClickListener false
        }
        popup.show()
    }

    private fun changeStatus(volunteer: Volunteer) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(R.string.change_status)
                .setItems(
                        arrayOf(
                                context.getString(R.string.volunteer_status_active),
                                context.getString(R.string.volunteer_status_left) )
                ) { _, which ->
                    when (which) {
                        0 -> {
                            volunteer.status = context.getString(R.string.volunteer_status_active)
                            viewModel.insertVolunteer(volunteer)
                            onChangeVolunteerStatusListener?.onStatusChanged(volunteer)
                        }
                        1 -> {
                            volunteer.status = context.getString(R.string.volunteer_status_left)
                            viewModel.insertVolunteer(volunteer)
                            onChangeVolunteerStatusListener?.onStatusChanged(volunteer)
                        }
                    }
                }
        dialogBuilder.create().show()
    }

    private fun setTime(volunteer: Volunteer) {
        val activity = context as AppCompatActivity
        val timePicker = TimePickerFragment(TimePickerDialog.OnTimeSetListener {
            _, hourOfDay, minute ->

            val hour = if (hourOfDay.toString().length == 2) hourOfDay.toString()
            else "0$hourOfDay"
            val min = if (minute.toString().length == 2) minute.toString()
            else "0$minute"
            volunteer.timeForSearch = "$hour:$min"
            viewModel.insertVolunteer(volunteer)
            onVolunteerChangeTimeToSearchListener?.onTimeChanged(volunteer)
        })
        timePicker.show(activity.supportFragmentManager, "timePicker")
    }

    interface OnChangeVolunteerStatusListener {
        fun onStatusChanged(volunteer: Volunteer)
    }

    interface OnVolunteerChangeTimeToSearchListener {
        fun onTimeChanged(volunteer: Volunteer)
    }

}