package ru.kireev.mir.registrarlizaalert.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.kireev.mir.registrarlizaalert.R;
import ru.kireev.mir.registrarlizaalert.data.Volunteer;

public class VolunteerAutoCompleteAdapter_old_java extends BaseAdapter implements Filterable {
    private Context context;
    private List<Volunteer> results;
    private List<Volunteer> fullList;

    public VolunteerAutoCompleteAdapter_old_java(Context context, List<Volunteer> fullList) {
        this.context = context;
        results = new ArrayList<>();
        this.fullList = fullList;
    }

    public void setFullList(List<Volunteer> fullList) {
        this.fullList = fullList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Volunteer getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.auto_complete_member_item, parent, false);
        }
        Volunteer volunteer = getItem(position);
        TextView firstLastName = convertView.findViewById(R.id.tvACTVItemFirstLastName);
        TextView callName = convertView.findViewById(R.id.tvACTVItemCallName);
        firstLastName.setText(String.format("%s %s", volunteer.getName(), volunteer.getSurname()));
        callName.setText(volunteer.getCallSign());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Volunteer> volunteers = findVolunteers(constraint);
                    filterResults.values = volunteers;
                    filterResults.count = volunteers.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    VolunteerAutoCompleteAdapter_old_java.this.results = (List<Volunteer>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<Volunteer> findVolunteers(CharSequence constraint){
        List<Volunteer> listForResult = new ArrayList<>();
        String stringConstraint = constraint.toString().toLowerCase();
        for (Volunteer volunteer : fullList){
            if (volunteer.getName().toLowerCase().contains(stringConstraint)
            || volunteer.getSurname().toLowerCase().contains(stringConstraint)
            || volunteer.getCallSign().toLowerCase().contains(stringConstraint)){
                listForResult.add(volunteer);
            }
        }

        return listForResult;
    }
}
