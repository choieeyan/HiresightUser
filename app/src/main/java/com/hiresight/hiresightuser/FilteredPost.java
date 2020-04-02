package com.hiresight.hiresightuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class FilteredPost extends AppCompatActivity {
    RecyclerView recyclerView;
    private String searchText;
    private FirebaseFirestore db;
    private CollectionReference reference;
    FirestoreRecyclerAdapter adapter;
    PostAdapter postAdapter;
    private OnItemClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_post);
        Intent intent = getIntent();
        searchText = intent.getStringExtra("searchText");
        db = FirebaseFirestore.getInstance();
        reference = db.collection("Client Posts");
        recyclerView = findViewById(R.id.filter_view);

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = reference.orderBy("postDateTime", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ModelPost> options = new FirestoreRecyclerOptions.Builder<ModelPost>()
                .setQuery(query, ModelPost.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ModelPost, FilterHolder>(options) {
            @NonNull
            @Override
            public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_post_layout, parent, false);
                return new FilterHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FilterHolder holder, int position, @NonNull final ModelPost model) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference ref = db.collection("Clients").document(model.getClientID());

                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String imageURL = document.getString("imageURL");
                                Picasso.get().load(imageURL).into(holder.clientImage);
                            } else
                                Log.d("TAG", "No such document");
                        } else
                            Log.d("TAG", "get failed with ", task.getException());
                    }
                });
                holder.companyName.setText(model.getCompanyName());
                holder.hiredate.setText(model.getStartDate()+ " til " +model.getEndDate());
                holder.location.setText(model.getLocation());
                holder.pay.setText(model.getPay());
                holder.product.setText(model.getProduct());
                holder.paxRequired.setText(model.getPaxRequired());
                holder.profession.setText(model.getProfession());

                if (model.getCompanyName().equals(searchText) || model.getLocation().equals(searchText) || model.getProduct().equals(searchText) || model.getProfession().equals(searchText)){
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                } else{
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                holder.chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), UserMessageActivity.class);
                        intent.putExtra("ClientID", model.getClientID());
                        startActivity(intent);
                        }
                    });

                holder.apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String postID = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                        Intent intent = new Intent(getApplicationContext(), ApplyActivity.class);
                        intent.putExtra("postID", postID);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView clientImage;
        TextView companyName, hiredate, location, product, pay, paxRequired, profession;
        Button apply, chat;


        public FilterHolder(@NonNull View itemView) {
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

        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
