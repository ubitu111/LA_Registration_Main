package ru.kireev.mir.laregistrationmain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kireev.mir.laregistrationmain.R;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {
    List<Volunteer> volunteers;

    public VolunteerAdapter() {
        volunteers = new ArrayList<>();
    }

    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.volunteer_item, parent, false);
        return new VolunteerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position) {
        Volunteer volunteer = volunteers.get(position);
        holder.textViewPosition.setText(Integer.toString(position + 1));
        holder.textViewName.setText(volunteer.getName());
        holder.textViewSurname.setText(volunteer.getSurname());
        holder.textViewCallSign.setText(volunteer.getCallSign());
        holder.textViewPhoneNumber.setText(volunteer.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return volunteers.size();
    }

    class VolunteerViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewSurname;
        private TextView textViewCallSign;
        private TextView textViewPhoneNumber;
        private TextView textViewPosition;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewSurname = itemView.findViewById(R.id.textViewSurname);
            textViewCallSign = itemView.findViewById(R.id.textViewCallSign);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
            textViewPosition = itemView.findViewById(R.id.textViewPosition);
        }
    }

    public List<Volunteer> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<Volunteer> volunteers) {
        this.volunteers = volunteers;
        notifyDataSetChanged();
    }
}
