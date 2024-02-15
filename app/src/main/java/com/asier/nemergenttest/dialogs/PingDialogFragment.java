package com.asier.nemergenttest.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.asier.nemergenttest.R;
import com.asier.nemergenttest.databinding.FragmentDialogPingBinding;
import com.asier.nemergenttest.ws.PingGoogleWS;

public class PingDialogFragment extends DialogFragment {

    private FragmentDialogPingBinding mBinding;
    private Boolean isLoopActive = false;
    private Integer totalSuccess = 0;
    private Integer totalFailures = 0;
    private Integer numTries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentDialogPingBinding.inflate(getLayoutInflater());
        mBinding.startButton.setOnClickListener(view -> {
            Log.d("dev-FLAG", "START BUTTON ON CLICK LISTENER");
            this.startLoop(
                    Integer.parseInt(mBinding.numTriesEditText.getText().toString())
            );
        });
        mBinding.stopButton.setOnClickListener(view -> {
            Log.d("dev-FLAG", "STOP BUTTON ON CLICK LISTENER");
            this.stopLoop();
        });
        mBinding.closeButton.setOnClickListener(view -> {
            this.dismiss();
        });
        return mBinding.getRoot();
    }

    public void startPings(Integer numTries) {
        Data pingData = new Data.Builder()
                .putInt("numTries", numTries)
                .build();
        OneTimeWorkRequest pingSystemOtwr= new OneTimeWorkRequest.Builder(PingGoogleWS.class)
                .setInputData(pingData)
                .addTag("pingTask")
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(pingSystemOtwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String successes = workInfo.getOutputData().getString("totalSuccesses");
                            String failures = workInfo.getOutputData().getString("totalFailures");
                            if (successes != null && failures != null) {
                                showResults(successes, failures);
                            }
                        }
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(pingSystemOtwr);

    }

    public void startLoop(Integer numTries) {
        if (!isLoopActive) { // Start the loop
            isLoopActive = true;
            this.totalFailures = 0;
            this.totalSuccess = 0;
            this.numTries = numTries;
            startPings(numTries);
        }
    }

    public void stopLoop() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag("pingTask");
    }

    public void showResults(String successes, String failures) {
        String successText = getString(R.string.successes) + ": " + successes;
        mBinding.SuccessesText.setText(successText);
        String failuresText = getString(R.string.failures) + ": " + failures;
        mBinding.FailuresText.setText(failuresText);
    }

}
