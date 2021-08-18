package com.pt.cam.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.cam.Model.Modes;
import com.pt.cam.R;
import com.pt.cam.databinding.EntitySelectedBinding;

import java.util.ArrayList;

public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.ViewHolder>{

    Context context;
    ArrayList<Modes> arrayList_Modes;

    public ModeAdapter(Context context, ArrayList<Modes> arrayList_Modes) {
        this.context = context;
        this.arrayList_Modes = arrayList_Modes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(EntitySelectedBinding.inflate((LayoutInflater.from(context)),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(arrayList_Modes.get(position).isSelected()){
            holder.binding.rlMain.setBackground(context.getDrawable(R.drawable.selected_back));
//            holder.binding.tvMode.setTextSize(15);
        }else{
            holder.binding.rlMain.setBackground(new ColorDrawable(Color.TRANSPARENT));

        }

        holder.binding.rlMain.setOnClickListener(v -> {
            changeStatus(position);
            notifyDataSetChanged();
        });

        holder.binding.tvMode.setText(arrayList_Modes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList_Modes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        EntitySelectedBinding binding;

        public ViewHolder(EntitySelectedBinding b) {
            super(b.getRoot());
            binding=b;
        }
    }

    public void changeStatus(int pos){
        for (int i = 0; i < arrayList_Modes.size(); i++) {
            arrayList_Modes.get(i).setSelected(i == pos);
        }
    }
}
