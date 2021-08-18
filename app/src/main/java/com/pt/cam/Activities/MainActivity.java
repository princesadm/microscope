package com.pt.cam.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pt.cam.Fragment.EducationFragment;
import com.pt.cam.Fragment.ProgramFragment;
import com.pt.cam.Fragment.SettingFragment;
import com.pt.cam.R;
import com.pt.cam.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.purple_500));
        changeFragment(new ProgramFragment(),"Device List");
        binding.betAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,CamaraActivity.class));
//            finish();
        });

        binding.llProgram.setOnClickListener(v -> changeSwipe(1));
        binding.llDevice.setOnClickListener(v -> changeSwipe(2));
        binding.llSetting.setOnClickListener(v -> changeSwipe(3));
    }

    public void changeSwipe(int pos){
        switch (pos){
            case 1:
                changeBackColor(binding.tvProgram,binding.ivProgram,true);
                changeBackColor(binding.tvDevice,binding.ivDevice,false);
                changeBackColor(binding.tvSetting,binding.ivSetting,false);
                changeFragment(new ProgramFragment(),"Device List");
                break;
            case 2:
                changeBackColor(binding.tvProgram,binding.ivProgram,false);
                changeBackColor(binding.tvDevice,binding.ivDevice,true);
                changeBackColor(binding.tvSetting,binding.ivSetting,false);
                changeFragment(new EducationFragment(),"Education");
                break;
            case 3:
                changeBackColor(binding.tvProgram,binding.ivProgram,false);
                changeBackColor(binding.tvDevice,binding.ivDevice,false);
                changeBackColor(binding.tvSetting,binding.ivSetting,true);
                changeFragment(new SettingFragment(),"Setting");
                break;
            default:
                changeBackColor(binding.tvProgram,binding.ivProgram,true);
                changeBackColor(binding.tvDevice,binding.ivDevice,false);
                changeBackColor(binding.tvSetting,binding.ivSetting,false);
                changeFragment(new ProgramFragment(),"Device List");
        }
    }

    public void changeFragment(Fragment fragment,String text){
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment,fragment);
        ft.commit();
        binding.tvWelcome.setText(text);
    }

    public void changeBackColor(TextView textView,ImageView imageView,boolean isSelected){
        if(isSelected){
            textView.setTextColor(getResources().getColor(R.color.purple_500));
            imageView.setColorFilter(ContextCompat.getColor(MainActivity.this,
                    R.color.purple_500));
        }else{
            textView.setTextColor(getResources().getColor(R.color.colorButtonGreyBg));
            imageView.setColorFilter(ContextCompat.getColor(MainActivity.this,
                    R.color.colorButtonGreyBg));
        }
    }
}