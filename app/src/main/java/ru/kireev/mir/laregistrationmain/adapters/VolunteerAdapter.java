package ru.kireev.mir.laregistrationmain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kireev.mir.laregistrationmain.R;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {
    List<Volunteer> volunteers;
    private OnVolunteerLongClickListener onVolunteerLongClickListener;

    public void setOnVolunteerLongClickListener(OnVolunteerLongClickListener onVolunteerLongClickListener) {
        this.onVolunteerLongClickListener = onVolunteerLongClickListener;
    }

    public interface OnVolunteerLongClickListener {
        void onLongClick(int position);
    }

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
        if (!volunteer.getCarMark().isEmpty()) {
            holder.linearLayoutFirstGroupInfoCar.setVisibility(View.VISIBLE);
            holder.linearLayoutSecondGroupInfoCar.setVisibility(View.VISIBLE);
            holder.textViewCarMark.setText(volunteer.getCarMark());
            holder.textViewCarModel.setText(volunteer.getCarModel());
            holder.textViewCarRegistrationNumber.setText(volunteer.getCarRegistrationNumber());
            holder.textViewCarColor.setText(volunteer.getCarColor());
        } else {
            holder.linearLayoutFirstGroupInfoCar.setVisibility(View.GONE);
            holder.linearLayoutSecondGroupInfoCar.setVisibility(View.GONE);
        }
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
        private TextView textViewCarMark;
        private TextView textViewCarModel;
        private TextView textViewCarRegistrationNumber;
        private TextView textViewCarColor;
        private LinearLayout linearLayoutFirstGroupInfoCar;
        private LinearLayout linearLayoutSecondGroupInfoCar;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewSurname = itemView.findViewById(R.id.textViewSurname);
            textViewCallSign = itemView.findViewById(R.id.textViewCallSign);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
            textViewPosition = itemView.findViewById(R.id.textViewPosition);
            textViewCarMark = itemView.findViewById(R.id.textViewCarMark);
            textViewCarModel = itemView.findViewById(R.id.textViewCarModel);
            textViewCarRegistrationNumber = itemView.findViewById(R.id.textViewCarRegistrationNumber);
            textViewCarColor = itemView.findViewById(R.id.textViewCarColor);
            linearLayoutFirstGroupInfoCar = itemView.findViewById(R.id.linearLayoutFirstGroupInfoCar);
            linearLayoutSecondGroupInfoCar = itemView.findViewById(R.id.linearLayoutSecondGroupInfoCar);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onVolunteerLongClickListener != null) {
                        onVolunteerLongClickListener.onLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
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
