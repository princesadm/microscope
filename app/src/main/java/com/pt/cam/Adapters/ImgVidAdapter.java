package com.pt.cam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.cam.Model.ImgVid;
import com.pt.cam.R;
import com.pt.cam.Utiles.AppUtils;
import com.pt.cam.databinding.EntityImgVidBinding;

import java.util.ArrayList;

public class ImgVidAdapter extends RecyclerView.Adapter<ImgVidAdapter.ViewHolder> {

    Context context;
    ArrayList<ImgVid> arrayList_ImgVids;

    public ImgVidAdapter(Context context, ArrayList<ImgVid> arrayList_ImgVids) {
        this.context = context;
        this.arrayList_ImgVids = arrayList_ImgVids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(EntityImgVidBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUtils.loadImage(context, holder.binding.ivImg, arrayList_ImgVids.get(position).getFile().getAbsolutePath());

        if (arrayList_ImgVids.get(position).isVideo()) {
            holder.binding.ivVid.setVisibility(View.VISIBLE);
        }

        holder.binding.ivOpt.setOnClickListener(view -> {
            showOptionDialog(context,holder);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_ImgVids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EntityImgVidBinding binding;

        public ViewHolder(EntityImgVidBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }


    public void showOptionDialog(Context mCtx, ViewHolder holder) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(mCtx, holder.binding.ivOpt);
        //inflating menu from xml resource
        popup.inflate(R.menu.image_option);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.maleriya:
                        //handle menu1 click
                        Toast.makeText(context, "maleriya", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.obaervation:
                        //handle menu2 click
                        Toast.makeText(context, "obaervation", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.size_dest:
                        //handle menu3 click
                        Toast.makeText(context, "size_dest", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.microworld:
                        //handle menu3 click
                        Toast.makeText(context, "microworld", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.delete:
                        //handle menu3 click
                        Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                        arrayList_ImgVids.remove(arrayList_ImgVids.get(holder.getAdapterPosition()));
                        notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            }
        });
        //displaying the popup
        popup.show();
    }

}
