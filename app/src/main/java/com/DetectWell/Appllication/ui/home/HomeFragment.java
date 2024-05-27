package com.DetectWell.Appllication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import androidx.navigation.Navigation;

import com.DetectWell.Appllication.R;
import com.DetectWell.Appllication.databinding.FragmentHomeBinding;
import com.DetectWell.Appllication.ui.userDataViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView greetingTextView, verifyTextView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private userDataViewModel viewModel;
    private CardView skinCancerCard, diabetesCard, heartCard, parkinsonCard ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(userDataViewModel.class);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize TextView
        greetingTextView = root.findViewById(R.id.text_home);
        verifyTextView = root.findViewById(R.id.verify_email);



        // Initialize CardView
        skinCancerCard = root.findViewById(R.id.skin_cancer_card);
        diabetesCard = root.findViewById(R.id.diabetes_card);
        heartCard = root.findViewById(R.id.heart_card);
        parkinsonCard = root.findViewById(R.id.parkinson_card);

        FirebaseUser user = auth.getCurrentUser();

        if (!user.isEmailVerified() && auth.getCurrentUser() != null  ){
            verifyTextView.setVisibility(View.VISIBLE);

            verifyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Unable to verify Email "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


        // Fetch user data from Firestore
        fetchUserData();

        // Set onClick listeners for the cards
        skinCancerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_skinCancer);
            }
        });
        diabetesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_diabetes);
            }
        });
        // Set onClick listeners for the cards
        heartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_heartDisease);
            }
        });
        // Set onClick listeners for the cards
        parkinsonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_parkinson);
            }
        });





        return root;
    }





    private void fetchUserData() {
        // Get the current user's UID
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Access the Firestore collection containing user data
            firestore.collection("User").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Retrieve username from Firestore
                                String username = document.getString("username");
                                String userEmail = document.getString("email");

                                viewModel.setUserName(username);
                                viewModel.setUserEmail(userEmail);

                                // Set the greeting text using the retrieved username
                                if (username != null) {
                                    greetingTextView.setText(getString(R.string.greeting_message, username));
                                }
                            }
                        } else {
                            // Handle errors
                            // For simplicity, just log the error
                            Exception e = task.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
