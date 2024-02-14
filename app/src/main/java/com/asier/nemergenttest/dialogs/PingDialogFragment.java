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
        return mBinding.getRoot();
    }

    public void makePing() {
        OneTimeWorkRequest pingSystemOtwr= new OneTimeWorkRequest.Builder(PingGoogleWS.class)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(pingSystemOtwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            String result = workInfo.getOutputData().getString("statusCode");
                            if (Integer.parseInt(result) == 200) {

                                Log.d("dev-PING RESULT 2", result);
                                totalSuccess ++;
                            }
                            else {
                                totalFailures ++;
                            }
                            Log.d("dev-NUM TRIES 2", numTries.toString());
                            Log.d("dev-IS LOOP ACTIVE 2", isLoopActive.toString());
                            Log.d("dev-TOTAL SUCCESSES", totalSuccess.toString());
                            Log.d("dev-TOTAL FAILURES", totalFailures.toString());
                            if (isLoopActive && numTries > 1) {
                                numTries --;
                                makePing();

                            } else {
                                stopLoop();
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
            makePing();
        }
    }

    public void stopLoop() {
        String successText = getString(R.string.successes) + ": " + totalSuccess.toString();
        mBinding.SuccessesText.setText(successText);
        String failuresText = getString(R.string.failures) + ": " + totalFailures.toString();
        mBinding.FailuresText.setText(failuresText);
    }

}
