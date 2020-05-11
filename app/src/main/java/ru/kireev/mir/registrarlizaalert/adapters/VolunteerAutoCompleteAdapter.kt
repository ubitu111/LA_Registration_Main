package ru.kireev.mir.registrarlizaalert.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import kotlinx.android.synthetic.main.auto_complete_member_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import java.util.*

class VolunteerAutoCompleteAdapter(private val context: Context, fullList: List<Volunteer>) : BaseAdapter(), Filterable {

    private var results: List<Volunteer> = arrayListOf()
    var fullList = fullList
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    init {
        this.fullList = fullList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.auto_complete_member_item, parent, false)
        val volunteer = getItem(position)
        val firstLastName = view.tvACTVItemFirstLastName
        val callName = view.tvACTVItemCallName
        firstLastName.text = String.format("%s %s", volunteer.name, volunteer.surname)
        callName.text = volunteer.callSign
        return view
    }

    override fun getItem(position: Int) = results[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = results.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val volunteers = findVolunteers(constraint)
                    filterResults.values = volunteers
                    filterResults.count = volunteers.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    this@VolunteerAutoCompleteAdapter.results = results.values as List<Volunteer>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    private fun findVolunteers(constraint: CharSequence): List<Volunteer> {
        val listForResult = mutableListOf<Volunteer>()
        val stringConstraint = constraint.toString().toLowerCase(Locale.getDefault())
        for (volunteer in fullList) {
            if (volunteer.name.toLowerCase(Locale.getDefault()).contains(stringConstraint)
                    || volunteer.surname.toLowerCase(Locale.getDefault()).contains(stringConstraint)
                    || volunteer.callSign.toLowerCase(Locale.getDefault()).contains(stringConstraint)) {
                listForResult.add(volunteer)
            }
        }
        return listForResult
    }
}