package com.asier.nemergenttest.ws;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PingGoogleWS extends Worker {

    public PingGoogleWS(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("dev-WORK STARTED", "yes");
        String endpoint = "https://www.google.com";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(endpoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            Log.d("dev-STATUS CODE", String.valueOf(statusCode));
            if (statusCode == 200) {

                Data results = new Data.Builder()
                        .putString("statusCode", String.valueOf(statusCode))
                        .build();
                return Result.success(results);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.failure();
        }
        return null;
    }

}
