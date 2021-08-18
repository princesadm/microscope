package com.pt.cam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.cam.Model.ImgVid;
import com.pt.cam.Utiles.AppUtils;
import com.pt.cam.databinding.EntityImgVidBinding;

import java.util.ArrayList;

public class ImgVidAdapter extends RecyclerView.Adapter<ImgVidAdapter.ViewHolder>{

    Context context;
    ArrayList<ImgVid> arrayList_ImgVids;

    public ImgVidAdapter(Context context, ArrayList<ImgVid> arrayList_ImgVids) {
        this.context = context;
        this.arrayList_ImgVids = arrayList_ImgVids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(EntityImgVidBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUtils.loadImage(context,holder.binding.ivImg,arrayList_ImgVids.get(position).getFile().getAbsolutePath());

        if(arrayList_ImgVids.get(position).isVideo()){
            holder.binding.ivVid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList_ImgVids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        EntityImgVidBinding binding;

        public ViewHolder(EntityImgVidBinding b) {
            super(b.getRoot());
            binding=b;
        }
    }
}
