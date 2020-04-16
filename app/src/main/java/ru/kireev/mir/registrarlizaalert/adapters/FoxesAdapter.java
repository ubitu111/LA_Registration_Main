package ru.kireev.mir.registrarlizaalert.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kireev.mir.registrarlizaalert.R;
import ru.kireev.mir.registrarlizaalert.data.Fox;

public class FoxesAdapter extends RecyclerView.Adapter<FoxesAdapter.FoxesViewHolder> {
    private List<Fox> foxes;

    public FoxesAdapter() {
        this.foxes = new ArrayList<>();
    }

    public List<Fox> getFoxes() {
        return foxes;
    }

    public void setFoxes(List<Fox> foxes) {
        this.foxes = foxes;
    }

    @NonNull
    @Override
    public FoxesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foxes_item, parent, false);
        return new FoxesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoxesViewHolder holder, int position) {
        Fox fox = foxes.get(position);
        holder.tvFoxesItemNumberTitle.setText(fox.getNumberOfFox());
        String name = fox.getElderOfFox().getName();
        String surname = fox.getElderOfFox().getSurname();
        String callSign = fox.getElderOfFox().getCallSign();
        String elder = String.format("%s %s (%s)", name, surname, callSign);
        holder.tvFoxesItemElder.setText(elder);
    }

    @Override
    public int getItemCount() {
        return foxes.size();
    }

    class FoxesViewHolder extends RecyclerView.ViewHolder{
        private TextView tvFoxesItemNumberTitle;
        private TextView tvFoxesItemElder;

        public FoxesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoxesItemNumberTitle = itemView.findViewById(R.id.tvFoxesItemNumberTitle);
            tvFoxesItemElder = itemView.findViewById(R.id.tvFoxesItemElder);
        }
    }
}
