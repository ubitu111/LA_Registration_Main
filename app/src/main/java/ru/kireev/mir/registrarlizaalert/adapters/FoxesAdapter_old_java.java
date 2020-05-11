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

public class FoxesAdapter_old_java extends RecyclerView.Adapter<FoxesAdapter_old_java.FoxesViewHolder_old_java> {
    private List<Fox> foxes;

    public FoxesAdapter_old_java() {
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
    public FoxesViewHolder_old_java onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foxes_item, parent, false);
        return new FoxesViewHolder_old_java(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoxesViewHolder_old_java holder, int position) {
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

    class FoxesViewHolder_old_java extends RecyclerView.ViewHolder{
        private TextView tvFoxesItemNumberTitle;
        private TextView tvFoxesItemElder;

        public FoxesViewHolder_old_java(@NonNull View itemView) {
            super(itemView);
            tvFoxesItemNumberTitle = itemView.findViewById(R.id.tvFoxesItemNumberTitle);
            tvFoxesItemElder = itemView.findViewById(R.id.tvFoxesItemElder);
        }
    }
}
