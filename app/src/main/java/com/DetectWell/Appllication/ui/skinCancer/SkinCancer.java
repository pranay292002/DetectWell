package com.DetectWell.Appllication.ui.skinCancer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.DetectWell.Appllication.LoginActivity;
import com.DetectWell.Appllication.MainActivity;
import com.DetectWell.Appllication.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.DetectWell.Appllication.databinding.FragmentSkinCancerBinding;
import com.DetectWell.Appllication.ml.Model;
import  org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;


public class SkinCancer extends Fragment {

    private FragmentSkinCancerBinding binding;
    Button selectImageBtn, captureImageBtn,getSkinResultsBtn;

    ImageView imageView;
    TextView skinResult;
    Bitmap bitmap;

    private final String[] classes = {
            "melanocytic nevi (Non-Cancerous)",
            "melanoma (Cancer)",
            "benign keratosis-like lesions (Non-Cancerous)",
            "basal cell carcinoma (Cancer)",
            "actinic keratoses and intraepithelial carcinomae (Cancer)",
            "Vascular lesions (Can lead to cancer)",
            "dermatofibroma (Non-Cancerous)"
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSkinCancerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        selectImageBtn = root.findViewById(R.id.skin_btn_select);
        captureImageBtn = root.findViewById(R.id.skin_btn_capture);
        getSkinResultsBtn = root.findViewById(R.id.skin_btn_results);
        skinResult = root.findViewById(R.id.skin_result_text);
        imageView = root.findViewById(R.id.skin_imageview);



        int imageResource = getResources().getIdentifier("@drawable/detectwell_logo", null, requireActivity().getPackageName());

        Drawable res = getResources().getDrawable(imageResource);
        imageView.setImageDrawable(res);


        getPermission();

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 31);

            }
        });



        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 29);
            }
        });


        getSkinResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null) {
                    Log.e("SkinCancer", "Bitmap is null");
                    return; // Return early if bitmap is null
                }

                Log.d("SkinCancer", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmap = Bitmap.createScaledBitmap(bitmap, 28, 28,true);

                try {
                    Model model = Model.newInstance(getContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 28, 28, 3}, DataType.FLOAT32);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                    // Releases model resources if no longer used.
                    model.close();

                    float[] probabilities = outputFeature0.getFloatArray();
                    int classIndex = getMax(probabilities);

                    // Get the class label corresponding to the class index
                    String className = classes[classIndex];
                    float confidence = probabilities[classIndex] * 100; // Convert probability to percentage

                    skinResult.setText(String.format("%.2f%% chances of having %s", confidence, className));


                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });



        return root;
    }


    public int getMax(float[] arr){
        int Maximum=0;
        for(int i=0; i<arr.length; i++){
            if(arr[i]> arr[Maximum]) Maximum=i;
        }
        return Maximum;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 31 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        } else if (requestCode == 29 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null && data.getExtras().containsKey("data")) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getActivity(), "Failed to capture image from camera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    void getPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(requireActivity().checkSelfPermission(android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),new String[] {Manifest.permission.CAMERA}, 8 );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 8) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied, request again
                getPermission();
            }
        }
    }


}