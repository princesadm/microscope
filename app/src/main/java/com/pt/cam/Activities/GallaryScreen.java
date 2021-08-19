package com.pt.cam.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.cam.Adapters.ImgVidAdapter;
import com.pt.cam.Model.ImgVid;
import com.pt.cam.R;
import com.pt.cam.Utiles.AppConstraint;
import com.pt.cam.databinding.ActivityGallaryScreenBinding;

import java.io.File;
import java.util.ArrayList;

public class GallaryScreen extends AppCompatActivity {

    private ActivityGallaryScreenBinding binding;
    private ArrayList<ImgVid> arrayList_ImgVids;
    private ImgVidAdapter imgVidAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGallaryScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(GallaryScreen.this, R.color.purple_500));

        arrayList_ImgVids = new ArrayList<>();

        changeData(1);

        binding.tvAll.setOnClickListener(v -> {
            changeTab(binding.tvAll);
            changeData(1);
        });
        binding.tvPhoto.setOnClickListener(v -> {
            changeTab(binding.tvPhoto);
            changeData(2);
        });
        binding.tvVideo.setOnClickListener(v -> {
            changeTab(binding.tvVideo);
            changeData(3);
        });
        binding.ivMenu.setOnClickListener(v -> onBackPressed());

    }

    public void changeData (int mode) {
        try {
            File file = new File(AppConstraint.IMG_FOLDER_PATH);
            switch (mode) {
                case 1:
                    if (file.listFiles().length > 0) {
                        arrayList_ImgVids = new ArrayList<ImgVid>();
                        for (int i = 0; i < file.listFiles().length; i++) {
                            arrayList_ImgVids.add(new ImgVid(file.listFiles()[i], false));
                        }
                        if (arrayList_ImgVids.size() > 0) {
                            imgVidAdapter = new ImgVidAdapter(GallaryScreen.this, arrayList_ImgVids);
                            binding.rvData.setLayoutManager(new GridLayoutManager(GallaryScreen.this, 3));
                            binding.rvData.setAdapter(imgVidAdapter);
                        } else {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 2:
                    if (file.listFiles().length > 0) {
                        arrayList_ImgVids = new ArrayList<ImgVid>();
                        for (int i = 0; i < file.listFiles().length; i++) {
                            arrayList_ImgVids.add(new ImgVid(file.listFiles()[i], false));
                        }
                        if (arrayList_ImgVids.size() > 0) {
                            imgVidAdapter = new ImgVidAdapter(GallaryScreen.this, arrayList_ImgVids);
                            binding.rvData.setLayoutManager(new GridLayoutManager(GallaryScreen.this, 3));
                            binding.rvData.setAdapter(imgVidAdapter);
                        } else {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 3:
                    if (file.listFiles().length > 0) {
                        arrayList_ImgVids = new ArrayList<ImgVid>();
                        for (int i = 0; i < file.listFiles().length; i++) {
                            arrayList_ImgVids.add(new ImgVid(file.listFiles()[i], true));
                        }
                        if (arrayList_ImgVids.size() > 0) {
                            imgVidAdapter = new ImgVidAdapter(GallaryScreen.this, arrayList_ImgVids);
                            binding.rvData.setLayoutManager(new GridLayoutManager(GallaryScreen.this, 3));
                            binding.rvData.setAdapter(imgVidAdapter);
                        } else {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                default:
                    Toast.makeText(GallaryScreen.this, "t", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeTab (TextView textView) {
        if (textView.getId() == binding.tvAll.getId()) {
            setBack(binding.tvAll,Color.WHITE,true);
            setBack(binding.tvPhoto,Color.BLACK,false);
            setBack(binding.tvVideo,Color.BLACK,false);
        } else if (textView.getId() == binding.tvPhoto.getId()) {
            setBack(binding.tvAll,Color.BLACK,false);
            setBack(binding.tvPhoto,Color.WHITE,true);
            setBack(binding.tvVideo,Color.BLACK,false);
        } else if (textView.getId() == binding.tvVideo.getId()) {
            setBack(binding.tvAll,Color.BLACK,false);
            setBack(binding.tvPhoto,Color.BLACK,false);
            setBack(binding.tvVideo,Color.WHITE,true);
        }
    }

    public void setBack (TextView textView,int color,boolean isBack) {
        textView.setTextColor(color);
        if (isBack)
            textView.setBackgroundResource(R.drawable.tab_back);
        else
            textView.setBackground(new ColorDrawable(Color.TRANSPARENT));
    }


}