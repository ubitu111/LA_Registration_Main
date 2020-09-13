package ru.kireev.mir.registrarlizaalert.adapters

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.volunteer_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerClickListener
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
                if (volunteer.fullName.toLowerCase(Locale.getDefault()).contains(it)
                        || volunteer.callSign.toLowerCase(Locale.getDefault()).contains(it)
                        || volunteer.car.toLowerCase(Locale.getDefault()).contains(it)
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
    var onVolunteerClickListener: OnVolunteerClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.volunteer_item, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun getItemCount() = volunteers.size


    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]
        with(holder) {
            textViewPosition.text = (position + 1).toString()
            textViewFullName.text = volunteer.fullName
            textViewCallSign.text = volunteer.callSign
            textViewNickname.text = volunteer.nickName
            textViewRegion.text = volunteer.region
            textViewPhoneNumber.text = volunteer.phoneNumber
            textViewCar.text = volunteer.car
            tvVolunteerStatus.text = volunteer.status
            when (volunteer.status) {
                context.getString(R.string.volunteer_status_active) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        tvVolunteerStatus.setTextColor(context.resources.getColor(R.color.green, context.theme))
                    else tvVolunteerStatus.setTextColor(context.resources.getColor(R.color.green))
                }
                context.getString(R.string.volunteer_status_left) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        tvVolunteerStatus.setTextColor(context.resources.getColor(R.color.red, context.theme))
                    else
                        tvVolunteerStatus.setTextColor(context.resources.getColor(R.color.red))
                }
            }
            tvVolunteerTimeForSearch.text = volunteer.timeForSearch

            tvOptionsVolunteerItem.setOnClickListener {
                showPopup(tvOptionsVolunteerItem, volunteer, adapterPosition)
            }
            textViewPhoneNumber.setOnClickListener {
                onVolunteerPhoneNumberClickListener?.onVolunteerPhoneNumberClick(textViewPhoneNumber.text.toString())
            }
        }

    }

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFullName: TextView = itemView.textViewFullName
        val textViewCallSign: TextView = itemView.textViewCallSign
        val textViewNickname: TextView = itemView.textViewNickname
        val textViewRegion: TextView = itemView.textViewRegion
        val textViewPhoneNumber: TextView = itemView.textViewPhoneNumber
        val textViewCar: TextView = itemView.textViewCar
        val textViewPosition: TextView = itemView.textViewPosition
        val tvVolunteerStatus: TextView = itemView.tvVolunteerStatus
        val tvOptionsVolunteerItem: TextView = itemView.tvOptionsVolunteerItem
        val tvVolunteerTimeForSearch: TextView = itemView.tvVolunteerTimeForSearch
    }

    private fun showPopup(textView: TextView, volunteer: Volunteer, position: Int) {
        val popup = PopupMenu(context, textView)
        popup.inflate(R.menu.volunter_item_options_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.options_menu_change_status -> {
                    changeStatus(volunteer, position)
                }
                R.id.options_menu_set_time -> {
                    setTime(volunteer, position)
                }
                R.id.options_menu_edit_data -> {
                    onVolunteerClickListener?.onVolunteerClick(position)
                }
            }
            return@setOnMenuItemClickListener false
        }
        popup.show()
    }

    private fun changeStatus(volunteer: Volunteer, position: Int) {
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
                            viewModel.updateVolunteer(volunteer)
                            onChangeVolunteerStatusListener?.onStatusChanged(position)
                        }
                        1 -> {
                            volunteer.status = context.getString(R.string.volunteer_status_left)
                            viewModel.updateVolunteer(volunteer)
                            onChangeVolunteerStatusListener?.onStatusChanged(position)
                        }
                    }
                }
        dialogBuilder.create().show()
    }

    private fun setTime(volunteer: Volunteer, position: Int) {
        val activity = context as AppCompatActivity
        val timePicker = TimePickerFragment(TimePickerDialog.OnTimeSetListener {
            _, hourOfDay, minute ->

            val hour = if (hourOfDay.toString().length == 2) hourOfDay.toString()
            else "0$hourOfDay"
            val min = if (minute.toString().length == 2) minute.toString()
            else "0$minute"
            volunteer.timeForSearch = "$hour:$min"
            viewModel.updateVolunteer(volunteer)
            onVolunteerChangeTimeToSearchListener?.onTimeChanged(position)
        })
        timePicker.show(activity.supportFragmentManager, "timePicker")
    }

    interface OnChangeVolunteerStatusListener {
        fun onStatusChanged(position: Int)
    }

    interface OnVolunteerChangeTimeToSearchListener {
        fun onTimeChanged(position: Int)
    }

}