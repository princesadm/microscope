package com.pt.cam.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pt.cam.Activities.CamaraActivity;
import com.pt.cam.Activities.MainActivity;
import com.pt.cam.R;
import com.pt.cam.databinding.FragmentProgramBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgramFragment extends Fragment {

    private FragmentProgramBinding binding;

    public ProgramFragment ( ) {
        // Required empty public constructor
    }

    public static ProgramFragment newInstance () {
        ProgramFragment fragment = new ProgramFragment();
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
        binding=FragmentProgramBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        binding.betAdd.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(),CamaraActivity.class));
//            finish();
        });
    }
}