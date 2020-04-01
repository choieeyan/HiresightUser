package com.hiresight.hiresightuser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserMessageListAdapter extends FirestoreRecyclerAdapter<Chatlist, UserMessageListAdapter.MessageHolder> {

    DocumentReference reference, ref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    private OnItemClickListener listener;

    public UserMessageListAdapter(@NonNull FirestoreRecyclerOptions<Chatlist> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageHolder messageHolder, int i, @NonNull final Chatlist chatlist) {
        auth = FirebaseAuth.getInstance();
        reference = db.collection("Messages").document(chatlist.getRecentMessage());
        ref = db.collection("Clients").document(chatlist.getClientID());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String message = document.getString("message");
                        messageHolder.messageText.setText(message);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String companyName = document.getString("name");
                        messageHolder.companyName.setText(companyName);
                        String imageURL = document.getString("imageURL");
                        Picasso.get().load(imageURL).into(messageHolder.client_image);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usermessagelist_layout, parent, false);
        return new MessageHolder(view);
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        public TextView companyName, messageText;
        public ImageView client_image;


        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            messageText = itemView.findViewById(R.id.messageText);
            client_image = itemView.findViewById(R.id.client_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }



}

