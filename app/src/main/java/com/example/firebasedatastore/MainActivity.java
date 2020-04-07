package com.example.firebasedatastore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class
MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    TextView emailVerificationText,emailid,phoneNumber;
    Button logoutButton,EmailVerifcatuionbutton;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logoutButton=findViewById(R.id.logoutButton);
        emailVerificationText=findViewById(R.id.emailVerificationText);
        EmailVerifcatuionbutton=findViewById(R.id.EmailVerifcatuionbutton);
        emailid=findViewById(R.id.emailid);
        phoneNumber=findViewById(R.id.phoneNumber);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        final FirebaseUser user=mAuth.getCurrentUser();
        userID=mAuth.getCurrentUser().getUid();
        DocumentReference documentReference=firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                emailid.setText(documentSnapshot.getString("email"));
                phoneNumber.setText(documentSnapshot.getString("phoneNumber"));

            }
        });
            if ((user != null) && !user.isEmailVerified()) {

                emailVerificationText.setVisibility(View.VISIBLE);
                EmailVerifcatuionbutton.setVisibility(View.VISIBLE);
                EmailVerifcatuionbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  Toast.makeText(MainActivity.this, "Verification Email send", Toast.LENGTH_SHORT).show();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                Toast.makeText(MainActivity.this, "I need to see th click", Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Verification Email Not Send" + e.getMessage());
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(this, "cURRENT uSER is no user", Toast.LENGTH_SHORT).show();
            }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}

