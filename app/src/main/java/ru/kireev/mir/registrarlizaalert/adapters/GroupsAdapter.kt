package ru.kireev.mir.registrarlizaalert.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.groups_item.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.Group
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnClickGroupOptionsMenu
import ru.kireev.mir.registrarlizaalert.listeners.OnGroupClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener
import ru.kireev.mir.registrarlizaalert.util.getGroupCallsignAsString

class GroupsAdapter(private val context: Context,
                    private val volunteersViewModel: VolunteersViewModel
) : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>() {
    var groups: List<Group> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onVolunteerPhoneNumberClickListener: OnVolunteerPhoneNumberClickListener? = null
    var onClickGroupOptionsMenu: OnClickGroupOptionsMenu? = null
    var onGroupClickListener: OnGroupClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.groups_item, parent, false)
        return GroupsViewHolder(view)
    }

    override fun getItemCount() = groups.size

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val group = groups[position]
        val callsignOfGroup = group.groupCallsign.getGroupCallsignAsString(context)
        val numberOfGroup = "$callsignOfGroup ${group.numberOfGroup}"
        with(holder) {
            tvGroupsItemNumberTitle.text = numberOfGroup
            tvDate.text = group.dateOfCreation
            val elderTemplate = context.resources.getString(R.string.elder_template)
            runBlocking {
                val job = launch {
                    val elder = volunteersViewModel.getVolunteerById(group.elderOfGroupId)
                    tvGroupsItemElder.text = String.format(
                            elderTemplate,
                            elder.fullName,
                            elder.callSign)
                    tvGroupsItemElderPhone.text = elder.phoneNumber
                }
                job.join()
            }

            tvOptionsGroupItem.setOnClickListener {
                onClickGroupOptionsMenu?.onGroupOptionsMenuClick(tvOptionsGroupItem, group)
            }
            tvGroupsItemElderPhone.setOnClickListener {
                onVolunteerPhoneNumberClickListener?.onVolunteerPhoneNumberClick(tvGroupsItemElderPhone.text.toString())
            }
            cardViewGroup.setOnClickListener {
                onGroupClickListener?.onGroupClick(adapterPosition)
            }
        }

    }

    inner class GroupsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGroupsItemNumberTitle: TextView = itemView.tvGroupsItemNumberTitle
        val tvGroupsItemElder: TextView = itemView.tvGroupsItemElder
        val tvGroupsItemElderPhone: TextView = itemView.tvGroupsItemElderPhone
        val cardViewGroup: CardView = itemView.cardViewGroup
        val tvOptionsGroupItem: TextView = itemView.tvOptionsGroupItem
        val tvDate: TextView = itemView.tvDate
    }

}