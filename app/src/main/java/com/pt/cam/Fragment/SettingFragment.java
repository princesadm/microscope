package com.pt.cam.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pt.cam.Activities.AboutUsScreen;
import com.pt.cam.Activities.ContactUsScreen;
import com.pt.cam.R;
import com.pt.cam.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    public SettingFragment ( ) {
        // Required empty public constructor
    }


    public static SettingFragment newInstance () {
        SettingFragment fragment = new SettingFragment();

        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSettingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.llNotification.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), AboutUsScreen.class));
        });

        binding.llLanguage.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), ContactUsScreen.class));
        });
    }
}