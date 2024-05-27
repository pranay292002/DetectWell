package com.DetectWell.Appllication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.DetectWell.Appllication.ui.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText signupEmail, signupPassword, signUpUsername, confirmPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signUpUsername= findViewById(R.id.signUpUsername);
        confirmPassword= findViewById(R.id.confirmPassword);
        progressBar= findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String user = signUpUsername.getText().toString().trim();
                String confirmPass = confirmPassword.getText().toString().trim();




                if (user.isEmpty()){
                    signUpUsername.setError("Username cannot be empty");
                    progressBar.setVisibility(View.GONE);
                }

                else if (email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                    progressBar.setVisibility(View.GONE);
                }
                else if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                    progressBar.setVisibility(View.GONE);
                }
                else if (!pass.equals(confirmPass)) {
                    confirmPassword.setError("Passwords are not matching");
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    auth.createUserWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    firebaseFirestore.collection("User")
                                            .document(auth.getUid())
                                            .set(new UserModel(user, email));

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                           });
                   }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

    }
}