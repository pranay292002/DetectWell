package com.DetectWell.Appllication.ui.diabetes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.DetectWell.Appllication.R;
import com.DetectWell.Appllication.databinding.FragmentDiabetesBinding;
import org.json.JSONObject;

import android.util.Log;

import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Diabetes extends Fragment {

    private TextView valueTextView;
    private SeekBar slider;
    private EditText glucose, bloodPressure, skinThickness, insulin, bmi, pedigreeFunction, age;
    private Button getResultButton;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private FragmentDiabetesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDiabetesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        valueTextView = root.findViewById(R.id.value_text_pregnancy);
        slider = root.findViewById(R.id.slider_pregnancies);
        glucose = root.findViewById(R.id.glucose);
        bloodPressure = root.findViewById(R.id.blood_pressure);
        skinThickness= root.findViewById(R.id.skin_thikness);
        insulin = root.findViewById(R.id.insulin);
        bmi = root.findViewById(R.id.bmi);
        pedigreeFunction = root.findViewById(R.id.pedigree_function);
        age = root.findViewById(R.id.age);
        getResultButton = root.findViewById(R.id.diabetes_btn_result);
        resultTextView = root.findViewById(R.id.diabetes_result_text);
        progressBar = root.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);


        // Set listener for slider value changes
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the text view with the selected value
                valueTextView.setText("Number of pregnancies: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        // Handle button click to get results
        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!validateInputs()) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Prepare JSON object with user inputs
                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("pregnancies", slider.getProgress());
                    requestData.put("glucose", Integer.parseInt(glucose.getText().toString()));
                    requestData.put("blood_pressure", Integer.parseInt(bloodPressure.getText().toString()));
                    requestData.put("skin_thickness", Integer.parseInt(skinThickness.getText().toString()));
                    requestData.put("insulin", Integer.parseInt(insulin.getText().toString()));
                    requestData.put("bmi", Float.parseFloat(bmi.getText().toString()));
                    requestData.put("diabetes_pedigree_function", Float.parseFloat(pedigreeFunction.getText().toString()));
                    requestData.put("age", Integer.parseInt(age.getText().toString()));
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                // Make POST request to API
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, requestData.toString());
                Request request = new Request.Builder()
                        .url("https://diabetes-detection-flask-api.onrender.com/predict")
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
        // Validate all input fields
        if (glucose.getText().toString().isEmpty() || bloodPressure.getText().toString().isEmpty() ||
                skinThickness.getText().toString().isEmpty() || insulin.getText().toString().isEmpty() ||
                bmi.getText().toString().isEmpty() || pedigreeFunction.getText().toString().isEmpty() ||
                age.getText().toString().isEmpty()) {
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