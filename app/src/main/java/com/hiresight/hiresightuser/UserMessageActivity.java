package com.hiresight.hiresightuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserMessageActivity extends AppCompatActivity {

    private Button sendBtn;
    private EditText messageSend;
    private FirebaseAuth auth;
    private FirebaseFirestore messageDB, retrieveDB;
    private String clientID;
    RecyclerView recyclerView;
    private CollectionReference retrieveRef;
    private UserMessageAdapter userMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        auth = FirebaseAuth.getInstance();
        messageDB = FirebaseFirestore.getInstance();
        sendBtn = (Button) findViewById(R.id.sendBtn);
        messageSend = (EditText) findViewById((R.id.messageSend));
        Intent intent = getIntent();
        clientID = intent.getStringExtra("ClientID");
        retrieveDB = FirebaseFirestore.getInstance();
        retrieveRef = retrieveDB.collection("Messages");
        recyclerView = findViewById(R.id.recycler_view);
        storeMessage();

        setUpRecyclerView();

    }

    public void storeMessage(){
        sendBtn.setOnClickListener(new View.OnClickListener(){
            String documentID;

            @Override
            public void onClick(View v) {
                final String userID = auth.getCurrentUser().getUid();
                String messageDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String messageTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                final String messageDateTime = messageDate + " " + messageTime;
                UserMessage message = new UserMessage(userID, clientID, messageSend.getText().toString(), messageDateTime);

                messageDB.collection("Messages")
                        .add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentID = documentReference.getId();
                                Map<String, Object> recentMessage = new HashMap<>();
                                recentMessage.put("recentMessage", documentID);
                                recentMessage.put("timeStamp", messageDateTime);
                                recentMessage.put("clientID", clientID);
                                messageDB.collection("Users").document(userID)
                                        .collection("Chatlist").document()
                                        .set(recentMessage);

                                Map<String, Object> receiveMessage = new HashMap<>();
                                receiveMessage.put("recentMessage", documentID);
                                receiveMessage.put("timeStamp", messageDateTime);
                                receiveMessage.put("userID", userID);
                                messageDB.collection("Clients").document(clientID)
                                        .collection("Chatlist").document()
                                        .set(receiveMessage);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error in sending message! Please try again..", Toast.LENGTH_LONG).show();
                            }

                        });


            }
        });
    }


/*
    public void displayMessage() {
        final List<UserMessage> msgList = new ArrayList<>();
        retrieveRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                msgList.clear();
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        UserMessage msg = doc.getDocument().toObject(UserMessage.class);
                        msgList.add(msg);
                        userMessageAdapter.notifyDataSetChanged();
                    }
                }
                userMessageAdapter = new UserMessageAdapter(msgList);
                recyclerView.setAdapter(userMessageAdapter);
            }
        });
    }
*/



    private void setUpRecyclerView(){
        Query query = retrieveRef.whereEqualTo("senderID", auth.getUid())
                                 .whereEqualTo("receiverID", clientID)
                                 .orderBy("dateTime", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<UserMessage> options = new FirestoreRecyclerOptions.Builder<UserMessage>()
                .setQuery(query, UserMessage.class)
                .build();

        userMessageAdapter = new UserMessageAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userMessageAdapter);
    }



    @Override
    public void onStart() {
        super.onStart();
        if(userMessageAdapter!=null)
            userMessageAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(userMessageAdapter!=null)
            userMessageAdapter.stopListening();
    }
}

