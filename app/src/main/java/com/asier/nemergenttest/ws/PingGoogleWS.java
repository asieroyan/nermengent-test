package com.asier.nemergenttest.ws;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.net.HttpURLConnection;
import java.net.URL;

public class PingGoogleWS extends Worker {
    private final SharedPreferences sharedPrefs =  getApplicationContext().getSharedPreferences("com.asier.nemergenttest", Context.MODE_PRIVATE);

    public PingGoogleWS(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String endpoint = "https://www.google.com";
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(endpoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (isStopped()) {
                return Result.success();
            }
            if (statusCode == 200) {
                int tempSuccess = sharedPrefs.getInt("totalSuccesses", 0);
                tempSuccess++;
                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

                prefsEditor.putInt("totalSuccesses", tempSuccess);

                prefsEditor.commit();
            } else {
                int tempFailures = sharedPrefs.getInt("totalFailures", 0);
                tempFailures++;
                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

                prefsEditor.putInt("totalFailures", tempFailures);

                prefsEditor.commit();
            }
            if (sharedPrefs.getInt("triesLeft", 0) > 1) {
                int tempTries = sharedPrefs.getInt("triesLeft", 0);
                tempTries--;
                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

                prefsEditor.putInt("triesLeft", tempTries);

                prefsEditor.commit();
                doWork();
            }
            setEndState();
            return Result.success();
        } catch (
                Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
    public void setEndState() {
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean("isEnd", true);
        prefsEditor.commit();
    }
}
