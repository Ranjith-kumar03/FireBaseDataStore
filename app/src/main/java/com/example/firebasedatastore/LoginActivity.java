package com.example.firebasedatastore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText usernameIDlogin,passwordIDlogin;
    Button loginBtnID;
    TextView movetoRegistrationpage,resetPassword;
    private FirebaseAuth mAuth;
    private ProgressBar loginprogressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameIDlogin=findViewById(R.id.usernameIDlogin);
        passwordIDlogin=findViewById(R.id.passwordIDlogin);
        loginBtnID=findViewById(R.id.loginBtnID);
        loginprogressBar=findViewById(R.id.loginprogressBar);
        resetPassword=findViewById(R.id.resetPassword);
        loginprogressBar.setVisibility(View.INVISIBLE);
        resetPassword.setOnClickListener(this);
        movetoRegistrationpage=findViewById(R.id.movetoRegistrationpage);
        mAuth=FirebaseAuth.getInstance();
        loginBtnID.setOnClickListener(this);
        movetoRegistrationpage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loginBtnID:
                validateAndLogIn();
                break;
            case R.id.movetoRegistrationpage:
                startActivity(new Intent(this,RegisterActivity.class));
                finish();
                break;
            case R.id.resetPassword:
                resetPassword(v);
                break;
        }

    }

    private void resetPassword(View v) {
        final EditText resetEmailTest=new EditText(v.getContext());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((v.getContext()));
        dialogBuilder.setView(resetEmailTest);
        dialogBuilder.setTitle("Want To Reset Password");
        dialogBuilder.setMessage("Please Provide Email Address for Reseting password");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.sendPasswordResetEmail( resetEmailTest.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Reset Link has been send to the provided email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this,"Cancelled Request" , Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog ad=dialogBuilder.create();
        ad.show();
    }

    private void validateAndLogIn() {

        String email=usernameIDlogin.getText().toString().trim();
        String password=passwordIDlogin.getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            usernameIDlogin.setError("Please Enter Username");
        }else if(TextUtils.isEmpty(password))
        {
            passwordIDlogin.setError("Please Enter Password");
        }else {
            loginprogressBar=findViewById(R.id.loginprogressBar);
            loginprogressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Sucessfully Logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else
                    {
                        loginprogressBar=findViewById(R.id.loginprogressBar);
                        loginprogressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}