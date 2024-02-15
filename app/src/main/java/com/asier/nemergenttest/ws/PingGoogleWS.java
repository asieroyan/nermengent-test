package com.asier.nemergenttest.ws;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PingGoogleWS extends Worker {

    private Integer totalSuccess = 0;
    private Integer totalFailures = 0;
    private Integer numTries;
    private Boolean first = true;

    public PingGoogleWS(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("dev-WORK STARTED", "yes");
        if (first) {
            first = false;
            numTries = getInputData().getInt("numTries", 0);
        }
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

                totalSuccess++;
            } else {
                totalFailures++;
            }
            Log.d("dev-NUM TRIES 2", numTries.toString());
            Log.d("dev-TOTAL SUCCESSES", totalSuccess.toString());
            Log.d("dev-TOTAL FAILURES", totalFailures.toString());
            if (numTries > 1) {
                numTries--;
                doWork();
            }

            Log.d("dev-RETURN RESULT", totalSuccess + ", " + totalFailures);
            Data results = new Data.Builder()
                    .putString("totalSuccesses", String.valueOf(totalSuccess))
                    .putString("totalFailures", String.valueOf(totalFailures))
                    .build();
            return Result.success(results);
        } catch (
                Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        first = true;
        totalSuccess = 0;
        totalFailures = 0;
    }
}
