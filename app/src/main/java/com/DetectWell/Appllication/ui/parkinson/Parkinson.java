package com.DetectWell.Appllication.ui.parkinson;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.DetectWell.Appllication.R;
import com.DetectWell.Appllication.databinding.FragmentHeartDiseaseBinding;
import com.DetectWell.Appllication.databinding.FragmentParkinsonBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Parkinson extends Fragment {

    private EditText mdvpFo, mdvpFhi, mdvpFlo, mdvpJitter, mdvpJitterAbs, mdvpRap,
            mdvpPpq, jitterDdp, mdvpShimmer, mdvpShimmerDb, shimmerApq3, shimmerApq5,
            mdvpApq, shimmerDda, nhr, hnr, rpde, dfa, spread1, spread2, d2, ppe;

    private Button getResultButton;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private FragmentParkinsonBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentParkinsonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mdvpFo = root.findViewById(R.id.edit_mdvp_fo);
        mdvpFhi = root.findViewById(R.id.edit_mdvp_fhi);
        mdvpFlo = root.findViewById(R.id.edit_mdvp_flo);
        mdvpJitter = root.findViewById(R.id.edit_mdvp_jitter);
        mdvpJitterAbs = root.findViewById(R.id.edit_mdvp_jitter_abs);
        mdvpRap = root.findViewById(R.id.edit_mdvp_rap);
        mdvpPpq = root.findViewById(R.id.edit_mdvp_ppq);
        jitterDdp = root.findViewById(R.id.edit_jitter_ddp);
        mdvpShimmer = root.findViewById(R.id.edit_mdvp_shimmer);
        mdvpShimmerDb = root.findViewById(R.id.edit_mdvp_shimmer_db);
        shimmerApq3 = root.findViewById(R.id.edit_shimmer_apq3);
        shimmerApq5 = root.findViewById(R.id.edit_shimmer_apq5);
        mdvpApq = root.findViewById(R.id.edit_mdvp_apq);
        shimmerDda = root.findViewById(R.id.edit_shimmer_dda);
        nhr = root.findViewById(R.id.edit_nhr);
        hnr = root.findViewById(R.id.edit_hnr);
        rpde = root.findViewById(R.id.edit_rpde);
        dfa = root.findViewById(R.id.edit_dfa);
        spread1 = root.findViewById(R.id.edit_spread1);
        spread2 = root.findViewById(R.id.edit_spread2);
        d2 = root.findViewById(R.id.edit_d2);
        ppe = root.findViewById(R.id.edit_ppe);

        getResultButton = root.findViewById(R.id.parkinson_btn_result);
        resultTextView = root.findViewById(R.id.parkinson_result_text);
        progressBar = root.findViewById(R.id.progressbar_parkinson);
        progressBar.setVisibility(View.GONE);

        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!validateInputs()) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("mdvp_fo_hz", Float.parseFloat(mdvpFo.getText().toString()));
                    requestData.put("mdvp_fhi_hz", Float.parseFloat(mdvpFhi.getText().toString()));
                    requestData.put("mdvp_flo_hz", Float.parseFloat(mdvpFlo.getText().toString()));
                    requestData.put("mdvp_jitter_percent", Float.parseFloat(mdvpJitter.getText().toString()));
                    requestData.put("mdvp_jitter_abs", Float.parseFloat(mdvpJitterAbs.getText().toString()));
                    requestData.put("mdvp_rap", Float.parseFloat(mdvpRap.getText().toString()));
                    requestData.put("mdvp_ppq", Float.parseFloat(mdvpPpq.getText().toString()));
                    requestData.put("jitter_ddp", Float.parseFloat(jitterDdp.getText().toString()));
                    requestData.put("mdvp_shimmer", Float.parseFloat(mdvpShimmer.getText().toString()));
                    requestData.put("shimmer_db", Float.parseFloat(mdvpShimmerDb.getText().toString()));
                    requestData.put("shimmer_apq3", Float.parseFloat(shimmerApq3.getText().toString()));
                    requestData.put("shimmer_apq5", Float.parseFloat(shimmerApq5.getText().toString()));
                    requestData.put("mdvp_apq", Float.parseFloat(mdvpApq.getText().toString()));
                    requestData.put("shimmer_dda", Float.parseFloat(shimmerDda.getText().toString()));
                    requestData.put("nhr", Float.parseFloat(nhr.getText().toString()));
                    requestData.put("hnr", Float.parseFloat(hnr.getText().toString()));
                    requestData.put("rpde", Float.parseFloat(rpde.getText().toString()));
                    requestData.put("dfa", Float.parseFloat(dfa.getText().toString()));
                    requestData.put("spread1", Float.parseFloat(spread1.getText().toString()));
                    requestData.put("spread2", Float.parseFloat(spread2.getText().toString()));
                    requestData.put("d2", Float.parseFloat(d2.getText().toString()));
                    requestData.put("ppe", Float.parseFloat(ppe.getText().toString()));
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                OkHttpClient client = new OkHttpClient();

                // Send POST request to Flask API
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, requestData.toString());
                Request request = new Request.Builder()
                        .url("https://flask-api-parkinson-disease.onrender.com/predict")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            throw new IOException("Unexpected code " + response);

                        }
                        final String responseBody = response.body().string();
                        Log.d("Response", responseBody);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                resultTextView.setText(responseBody);
                            }
                        });
                    }

                });
            }
        });

        return root;
    }

    private boolean validateInputs() {
        if (mdvpFo.getText().toString().isEmpty() || mdvpFhi.getText().toString().isEmpty() ||
                mdvpFlo.getText().toString().isEmpty() || mdvpJitter.getText().toString().isEmpty() ||
                mdvpJitterAbs.getText().toString().isEmpty() || mdvpRap.getText().toString().isEmpty() ||
                mdvpPpq.getText().toString().isEmpty() || jitterDdp.getText().toString().isEmpty() ||
                mdvpShimmer.getText().toString().isEmpty() || mdvpShimmerDb.getText().toString().isEmpty() ||
                shimmerApq3.getText().toString().isEmpty() || shimmerApq5.getText().toString().isEmpty() ||
                mdvpApq.getText().toString().isEmpty() || shimmerDda.getText().toString().isEmpty() ||
                nhr.getText().toString().isEmpty() || hnr.getText().toString().isEmpty() ||
                rpde.getText().toString().isEmpty() || dfa.getText().toString().isEmpty() ||
                spread1.getText().toString().isEmpty() || spread2.getText().toString().isEmpty() ||
                d2.getText().toString().isEmpty() || ppe.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
