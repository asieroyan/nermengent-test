package com.asier.nemergenttest.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.asier.nemergenttest.R;
import com.asier.nemergenttest.databinding.FragmentDialogPingBinding;
import com.asier.nemergenttest.ws.PingGoogleWS;

public class PingDialogFragment extends DialogFragment {

    private FragmentDialogPingBinding mBinding;
    private Boolean isStopped = false;

    private SharedPreferences sharedPrefs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentDialogPingBinding.inflate(getLayoutInflater());
        sharedPrefs = requireContext().getSharedPreferences("com.asier.nemergenttest", Context.MODE_PRIVATE);


        mBinding.startButton.setOnClickListener(view -> {
            if (mBinding.numTriesEditText.getText().toString().equals("")) {
                Toast.makeText(requireContext(), R.string.not_value_error, Toast.LENGTH_SHORT).show();
            } else {
                if (!isStopped) {
                    SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                    prefsEditor.putInt("triesLeft", Integer.parseInt(String.valueOf(mBinding.numTriesEditText.getText())));
                    prefsEditor.putInt("totalSuccesses", 0);
                    prefsEditor.putInt("totalFailures", 0);
                    prefsEditor.putBoolean("isEnd", false);
                    prefsEditor.commit();
                }
                this.startPings();
            }
        });

        mBinding.stopButton.setOnClickListener(view -> {
            isStopped = true;
            this.stopLoop();
        });

        mBinding.closeButton.setOnClickListener(view -> this.dismiss());

        return mBinding.getRoot();
    }

    public void startPings() {
        OneTimeWorkRequest pingSystemOtwr= new OneTimeWorkRequest.Builder(PingGoogleWS.class)
                .addTag("pingTask")
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(pingSystemOtwr.getId())
                .observe(this, workInfo -> {

                    if(workInfo != null && workInfo.getState().isFinished()){
                        if (sharedPrefs.getBoolean("isEnd", false)) {
                            isStopped = false;
                        }
                        showResults();
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(pingSystemOtwr);

    }

    public void stopLoop() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag("pingTask");
    }

    public void showResults() {
        String successText = getString(R.string.successes) + ": " + sharedPrefs.getInt("totalSuccesses", 0);
        mBinding.SuccessesText.setText(successText);
        String failuresText = getString(R.string.failures) + ": " + sharedPrefs.getInt("totalFailures", 0);
        mBinding.FailuresText.setText(failuresText);
    }

}
