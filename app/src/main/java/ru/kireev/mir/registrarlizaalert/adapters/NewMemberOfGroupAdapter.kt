package ru.kireev.mir.registrarlizaalert.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.new_member_of_group_item.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.listeners.OnClickDeleteNewMemberOfGroupListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerItemClickListener

class NewMemberOfGroupAdapter(private val adapter: VolunteerAutoCompleteAdapter) : RecyclerView.Adapter<NewMemberOfGroupAdapter.NewMemberViewHolder>() {

    private var isTyping = false

    var newMembers = arrayListOf<String>()
        set(value) {
            isTyping = false
            field = value
            notifyDataSetChanged()
        }

    fun addMember(text: String) {
        isTyping = false
        newMembers.add(text)
        notifyDataSetChanged()
    }

    fun deleteMember(position: Int) {
        isTyping = false
        newMembers.removeAt(position)
        notifyDataSetChanged()
    }

    var onClickDeleteMemberListener: OnClickDeleteNewMemberOfGroupListener? = null
    var onVolunteerItemClickListener: OnVolunteerItemClickListener? = null
    var textChangedListener: TextChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_member_of_group_item, parent, false)
        val member = NewMemberViewHolder(view)
        member.ivDeleteNewMember.setOnClickListener {
            onClickDeleteMemberListener?.onClickDeleteMember(member.adapterPosition)
        }
        member.actvNewMemberItem.setOnClickListener {
            adapter.filter.filter("")
            member.actvNewMemberItem.showDropDown()
            isTyping = true
        }
        member.actvNewMemberItem.setOnItemClickListener { parent, _, position, _ ->
            onVolunteerItemClickListener?.onItemClick(parent, position, member.adapterPosition)
        }
        member.actvNewMemberItem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (isTyping) {
                    textChangedListener?.beforeChanged(s)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isTyping) {
                    textChangedListener?.onChanged(s)
                }
            }
        })
        return member
    }

    override fun getItemCount() = newMembers.size


    override fun onBindViewHolder(holder: NewMemberViewHolder, position: Int) {
        val text = newMembers[position]
        holder.actvNewMemberItem.setText(text)
        holder.actvNewMemberItem.setAdapter(adapter)
    }

    inner class NewMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val actvNewMemberItem: AutoCompleteTextView = itemView.actvNewMemberItem
        val ivDeleteNewMember: ImageView = itemView.ivDeleteNewMember
    }

    interface TextChangedListener {
        fun beforeChanged(s: CharSequence?)
        fun onChanged(s: CharSequence?)
    }

}