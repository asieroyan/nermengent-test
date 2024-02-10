package com.asier.nemergenttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.asier.nemergenttest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}