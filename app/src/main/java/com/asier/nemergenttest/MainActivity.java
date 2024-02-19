package com.asier.nemergenttest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.res.Resources;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.asier.nemergenttest.databinding.ActivityMainBinding;
import com.asier.nemergenttest.dialogs.PingDialogFragment;
import com.asier.nemergenttest.models.Picture;
import com.asier.nemergenttest.utils.DBController;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.pingButton.setOnClickListener(view -> {
            DialogFragment pingDialog = new PingDialogFragment();
            pingDialog.show(getSupportFragmentManager(), "");
        });
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission_group.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission_group.LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission_group.STORAGE}, 1);
        }

        createCameraLauncher();

        mBinding.pictureButton.setOnClickListener(view -> {
            takePicture();
        });
    }

    private void createCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        assert data != null;
                        Bundle extras = data.getExtras();
                        File file = null;
                        try {
                            file = createImageFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        assert extras != null;
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(file);
                            assert imageBitmap != null;
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                            Location gps_loc, network_loc;

                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }

                            double latitude = 0.0;
                            double longitude = 0.0;

                            try {
                                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                if (gps_loc != null) {
                                    latitude = gps_loc.getLatitude();
                                    longitude = gps_loc.getLongitude();
                                } else if (network_loc != null) {
                                    latitude = network_loc.getLatitude();
                                    longitude = network_loc.getLongitude();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String location = "";

                            try {
                                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    location = addresses.get(0).getLocality();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            DBController db = new DBController(this);
                            db.insertPicture(
                                    new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en")).format(new Date()),
                                    location,
                                    file.getPath()
                            );
                            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }


    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }
}