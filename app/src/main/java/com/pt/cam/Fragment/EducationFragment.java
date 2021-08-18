package com.pt.cam.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pt.cam.R;
import com.pt.cam.databinding.FragmentEducationBinding;


public class EducationFragment extends Fragment {

    private FragmentEducationBinding binding;

    public EducationFragment ( ) {
        // Required empty public constructor
    }


    public static EducationFragment newInstance ( ) {
        EducationFragment fragment = new EducationFragment();

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
        binding = FragmentEducationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        binding.tvTitle.setText("The following were seen on a peripheral blood smear from a patient who had traveled to an East African game park.");
        binding.tvDesc.setText("Every week I will post a new Case, along with the answer to the previous case. Please feel free to write in with your answers, comments, and questions.");

    }
}