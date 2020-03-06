package com.hiresight.hiresightuser;

import android.content.Context;
import android.graphics.ColorSpace;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class PostAdapter extends FirestoreRecyclerAdapter<ModelPost, PostAdapter.PostHolder> {
    private OnItemClickListener listener;

    public PostAdapter(@NonNull FirestoreRecyclerOptions<ModelPost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostHolder postHolder, int i, @NonNull final ModelPost modelPost) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Clients").document(modelPost.getClientID());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String companyName = document.getString("name");
                        String imageURL = document.getString("imageURL");
                        modelPost.setCompanyName(companyName);  //dont really need this: can delete this in modelpost too.
                        postHolder.companyName.setText(modelPost.getCompanyName()); //straight set string companyName.
                        Picasso.get().load(imageURL).into(postHolder.clientImage);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


        postHolder.hiredate.setText(modelPost.getStartDate()+ " til " +modelPost.getEndDate());
        postHolder.location.setText(modelPost.getLocation());
        postHolder.pay.setText(modelPost.getPay());
        postHolder.product.setText(modelPost.getProduct());
        postHolder.paxRequired.setText(modelPost.getPaxRequired());
        postHolder.profession.setText(modelPost.getProfession());


    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_post_layout, parent, false);
        return new PostHolder(view);
    }

    class PostHolder extends RecyclerView.ViewHolder{
        ImageView clientImage;
        TextView companyName, hiredate, location, product, pay, paxRequired, profession;
        Button apply, chat;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            clientImage = itemView.findViewById(R.id.client_image);
            companyName = itemView.findViewById(R.id.company_name);
            hiredate = itemView.findViewById(R.id.hire_date);
            location = itemView.findViewById(R.id.location);
            product = itemView.findViewById(R.id.product);
            pay = itemView.findViewById(R.id.pay);
            paxRequired = itemView.findViewById(R.id.pax_required);
            profession = itemView.findViewById(R.id.profession);
            apply = itemView.findViewById(R.id.aplyBtn);
            chat = itemView.findViewById(R.id.chatBtn);

            chat.setOnClickListener(new View.OnClickListener() {
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
