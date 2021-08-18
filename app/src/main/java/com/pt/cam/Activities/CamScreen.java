package com.pt.cam.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pt.cam.databinding.ActivityCamScreenBinding;

public class CamScreen extends AppCompatActivity {

    private ActivityCamScreenBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCamScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}