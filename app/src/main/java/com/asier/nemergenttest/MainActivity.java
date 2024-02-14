package com.asier.nemergenttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;

import com.asier.nemergenttest.databinding.ActivityMainBinding;
import com.asier.nemergenttest.dialogs.PingDialogFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.pingButton.setOnClickListener(view -> {
            DialogFragment pingDialog = new PingDialogFragment();
            pingDialog.show(getSupportFragmentManager(), "");
        });
    }
}