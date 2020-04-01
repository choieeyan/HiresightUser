package com.hiresight.hiresightuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApplyActivity extends AppCompatActivity {
    Button submitBtn;
    private String postID, userID;
    private FirebaseFirestore db;
    private CollectionReference ref;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        submitBtn = findViewById(R.id.submitBtn);
        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        db = FirebaseFirestore.getInstance();
        ref = db.collection("Client Posts").document(postID).collection("Applicants");
        submitApplication();
    }

    public void submitApplication(){
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Date messageDateTime = new Date();
                Map<String, Object> application = new HashMap<>();
                application.put("dateTime", messageDateTime);
                ref.document(userID).set(application);
            }
        });
    }
}
