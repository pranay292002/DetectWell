package com.DetectWell.Appllication.ui.heartDisease;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.DetectWell.Appllication.R;
import com.DetectWell.Appllication.databinding.FragmentHeartDiseaseBinding;

import org.json.JSONException;
import org.json.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HeartDisease extends Fragment {

    private FragmentHeartDiseaseBinding binding;
    private EditText editTextAge, editTextRestingBP, editTextCholesterol, editTextMaxHeartRate, editTextSTDepression, editTextMajorVessels;
    private RadioGroup radioGroupSex, radioGroupChestPain, radioGroupRestingECG, radioGroupSTSegment, radioGroupThalassemia;
    private CheckBox checkBoxFastingBloodSugar, checkBoxExerciseAngina;
    private Button btnResult;
    private ProgressBar progressBarHeart;
    private TextView resultText;

    private OkHttpClient client;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHeartDiseaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize views
        editTextAge = root.findViewById(R.id.editTextAge);
        editTextRestingBP = root.findViewById(R.id.editTextRestingBP);
        editTextCholesterol = root.findViewById(R.id.editTextCholesterol);
        editTextMaxHeartRate = root.findViewById(R.id.editTextMaxHeartRate);
        editTextSTDepression = root.findViewById(R.id.editTextSTDepression);
        editTextMajorVessels = root.findViewById(R.id.editTextMajorVessels);
        radioGroupSex = root.findViewById(R.id.radioGroupSex);
        radioGroupChestPain = root.findViewById(R.id.radioGroupChestPain);
        radioGroupRestingECG = root.findViewById(R.id.radioGroupRestingECG);
        radioGroupSTSegment = root.findViewById(R.id.radioGroupSTSegment);
        radioGroupThalassemia = root.findViewById(R.id.radioGroupThalassemia);
        checkBoxFastingBloodSugar = root.findViewById(R.id.checkBoxFastingBloodSugar);
        checkBoxExerciseAngina = root.findViewById(R.id.checkBoxExerciseAngina);
        btnResult = root.findViewById(R.id.heart_btn_result);
        progressBarHeart = root.findViewById(R.id.progressbar_heart);
        resultText = root.findViewById(R.id.heart_result_text);

        progressBarHeart.setVisibility(View.GONE);

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Button click listener
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarHeart.setVisibility(View.VISIBLE);
                if (!validateInputs()) {
                    progressBarHeart.setVisibility(View.GONE);
                    return;
                }

                JSONObject json = new JSONObject();
                    try {

                    json.put("age", Integer.parseInt(editTextAge.getText().toString()));
                    json.put("sex", radioGroupSex.getCheckedRadioButtonId() == R.id.radioButtonMale ? 1 : 0);
                    json.put("chest_pain_type", getSelectedRadioButtonValue(radioGroupChestPain));
                    json.put("resting_blood_pressure", Integer.parseInt(editTextRestingBP.getText().toString()));
                    json.put("cholesterol", Integer.parseInt(editTextCholesterol.getText().toString()));
                    json.put("fasting_blood_sugar", checkBoxFastingBloodSugar.isChecked() ? 1 : 0);
                    json.put("resting_ecg", getSelectedRadioButtonValue(radioGroupRestingECG));
                    json.put("max_heart_rate", Integer.parseInt(editTextMaxHeartRate.getText().toString()));
                    json.put("exercise_induced_angina", checkBoxExerciseAngina.isChecked() ? 1 : 0);
                    json.put("st_depression", Float.parseFloat(editTextSTDepression.getText().toString()));
                    json.put("st_slope", getSelectedRadioButtonValue(radioGroupSTSegment));
                    json.put("num_major_vessels", Integer.parseInt(editTextMajorVessels.getText().toString()));
                    json.put("thal", getSelectedRadioButtonValue(radioGroupThalassemia));
                    } catch (JSONException e) {
                        progressBarHeart.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    // Send POST request to Flask API
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .url("https://heart-disease-detection-api.onrender.com/predict")
                            .post(body)
                            .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarHeart.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            progressBarHeart.setVisibility(View.GONE);
                            throw new IOException("Unexpected code " + response);

                        }
                        final String responseBody = response.body().string();
                        Log.d("Response", responseBody);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarHeart.setVisibility(View.GONE);
                                resultText.setText(responseBody);
                            }
                        });
                    }

                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Validate input fields
    private boolean validateInputs() {
        if (editTextAge.getText().toString().isEmpty() ||
                editTextRestingBP.getText().toString().isEmpty() ||
                editTextCholesterol.getText().toString().isEmpty() ||
                editTextMaxHeartRate.getText().toString().isEmpty() ||
                editTextSTDepression.getText().toString().isEmpty() ||
                editTextMajorVessels.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Get the value of the selected radio button in a RadioGroup
    private int getSelectedRadioButtonValue(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroup.findViewById(selectedId);
        if (radioButton != null) {
            return radioGroup.indexOfChild(radioButton);
        }
        return -1;
    }



}
