package com.example.customerrecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private TextView btnToSignup;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnToSignup = findViewById(R.id.btnToSignUp);
        edtEmail = findViewById(R.id.etLoginEmail);
        edtPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        btnToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup.class));
                finish();
            }
        });
    }

    private void loginUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in...");
        progressDialog.show();
        String strEmail = edtEmail.getText().toString();
        String strPassword = edtPassword.getText().toString();

        if(TextUtils.isEmpty(strEmail)){
            edtEmail.setError("Enter the email");
            edtEmail.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(strPassword)){
            edtPassword.setError("Enter the Password");
            edtPassword.requestFocus();
            progressDialog.dismiss();
            return;
        }


        firebaseAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Check Your Credentials", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}