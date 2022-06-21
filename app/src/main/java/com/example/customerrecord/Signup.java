package com.example.customerrecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    private TextView btnToLogin;
    private EditText fullName, phoneNumber, email, password, confirmPassword;
    private Button btnSignup;
    private CheckBox cbAgreement;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        btnToLogin = findViewById(R.id.btnToLogin);
        fullName = findViewById(R.id.edtFullName);
        phoneNumber = findViewById(R.id.edtPhoneNumber);
        email = findViewById(R.id.edtEmailAddress);
        password = findViewById(R.id.edtSignUpPassword);
        confirmPassword = findViewById(R.id.edtSignUpConfirmPassword);
        cbAgreement = findViewById(R.id.cbUserAgreement);
        btnSignup = findViewById(R.id.btnSignUp);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser();
            }
        });


        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Login.class));
                finish();
            }
        });

    }

    private void signupUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing Up...");
        progressDialog.show();

        String strFullName = fullName.getText().toString();
        String strPhoneNumber = phoneNumber.getText().toString();
        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();
        String strConfirmPassword = confirmPassword.getText().toString();

        if(!cbAgreement.isChecked()){
            Toast.makeText(this, "Accept the User Agreements", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(strFullName)){
            fullName.setError("Enter the FullName");
            fullName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(strPhoneNumber)){
            phoneNumber.setError("Enter the Phone Number");
            phoneNumber.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(strEmail)){
            email.setError("Enter the Email");
            email.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(strPassword)){
            password.setError("Enter your Password");
            password.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(strConfirmPassword)){
            confirmPassword.setError("Re-enter your Password");
            confirmPassword.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(strFullName.length() <= 3){
            fullName.setError("Name must be greater than 3 letters");
            fullName.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(strPassword.length() < 8){
            password.setError("Password must be of 8 or more letters ");
            password.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(!strPassword.matches(strConfirmPassword)){
            password.setError("Password doesn't match!");
            password.requestFocus();
            progressDialog.dismiss();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // If User Account Creation is Successful Create User Database
                    User user = new User(strFullName, strPhoneNumber, strEmail);
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(Signup.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Signup.this, MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(Signup.this, "Database Creation Failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }
                        }
                    });
                }else {
                    Toast.makeText(Signup.this, "User Creation Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
            }
        });

    }


}