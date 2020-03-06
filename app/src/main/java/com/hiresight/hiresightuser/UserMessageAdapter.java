package com.hiresight.hiresightuser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserMessageAdapter extends FirestoreRecyclerAdapter<UserMessage, UserMessageAdapter.MessageHolder> {

    public static final int MSG_RECEIVED = 0;
    public static final int MSG_SENT = 1;
    private FirebaseUser currentUser;


    public UserMessageAdapter(@NonNull FirestoreRecyclerOptions<UserMessage> options) {
        super(options);
    }



    @Override
    protected void onBindViewHolder(@NonNull final MessageHolder messageHolder, int i, @NonNull final UserMessage userMessage) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference reference = db.collection("Clients").document(userMessage.getReceiverID());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String imageURL = document.getString("imageURL");
                        Picasso.get().load(imageURL).into(messageHolder.user_image);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        messageHolder.messageText.setText(userMessage.getMessage());
        messageHolder.messageTime.setText(userMessage.getDateTime());

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_SENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersentmessage_layout, parent, false);
            return new MessageHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userreceivedmessage_layout, parent, false);
            return new MessageHolder(view);
        }

    }


    class MessageHolder extends RecyclerView.ViewHolder{
        ImageView user_image;
        TextView messageText, messageTime;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.user_image);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getItem(position).getSenderID().equals(currentUser.getUid())) {
            return MSG_SENT;
        }else {
            return MSG_RECEIVED;
        }

    }



}

