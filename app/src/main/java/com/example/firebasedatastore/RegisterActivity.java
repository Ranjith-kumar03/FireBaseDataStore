package com.example.firebasedatastore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="registerActivity" ;
    EditText usernameID,phonenumberID,passwordID;
    ProgressBar progressBarRegisterUser;
    Button registerBtnID;
    TextView clcikheretologin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameID=findViewById(R.id.usernameID);
        phonenumberID=findViewById(R.id.phonenumberID);
        passwordID=findViewById(R.id.passwordID);
        registerBtnID=findViewById(R.id.registerBtnID);
        clcikheretologin=findViewById(R.id.clcikheretologin);
        progressBarRegisterUser=findViewById(R.id.progressBarRegisterUser);
        progressBarRegisterUser.setVisibility(View.GONE);
        registerBtnID.setOnClickListener(this);
        clcikheretologin.setOnClickListener(this);
        fstore=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.registerBtnID:
                createUser();
                break;
            case R.id.clcikheretologin:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
        }

    }

    private void createUser() {

        final String email=usernameID.getText().toString().trim();
        final String password=passwordID.getText().toString().trim();
        final String phoneNumber=phonenumberID.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            usernameID.setError("Please Enter Username");
        }else if(TextUtils.isEmpty(password))
        {
            passwordID.setError("Please Enter Password");
        }else{
            progressBarRegisterUser=findViewById(R.id.progressBarRegisterUser);
            progressBarRegisterUser.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userID=mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference=fstore.collection("users").document(userID);
                                progressBarRegisterUser=findViewById(R.id.progressBarRegisterUser);
                                progressBarRegisterUser.setVisibility(View.INVISIBLE);
                                FirebaseAuth firebaseAuth;
                                FirebaseUser user=mAuth.getCurrentUser();
                                Map<String,Object> user_info=new HashMap<>();
                                user_info.put("email", email);
                                user_info.put("password",password);
                                user_info.put("phoneNumber", phoneNumber);
                                documentReference.set(user_info).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "User Details Sucessfully Added", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "Verification Email send", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Verification Email Not Send"+e.getMessage());
                                    }
                                });
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterActivity.this, "Sucessfully Created User", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                progressBarRegisterUser.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "Failure of User Creation", Toast.LENGTH_SHORT).show();

                            }

                        }


                    });
        }
    }
}
