package com.hiresight.hiresightuser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.hiresight.hiresightuser.RegisterActivity.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore db;
    private PostAdapter adapter;
    private CollectionReference reference;
    RecyclerView recyclerView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button chatBtn;
    private String companyName;

    private OnFragmentInteractionListener mListener;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        chatBtn = (Button) view.findViewById(R.id.chatBtn);
        db = FirebaseFirestore.getInstance();
        reference = db.collection("Client Posts");
        recyclerView = view.findViewById(R.id.recycler_view);

        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView(){
        Query query = reference.orderBy("postDateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ModelPost> options = new FirestoreRecyclerOptions.Builder<ModelPost>()
                .setQuery(query, ModelPost.class)
                .build();

        adapter = new PostAdapter(options, this.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
/*
        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ModelPost post = documentSnapshot.toObject(ModelPost.class);
                String id = documentSnapshot.getId(); //get id of database
                String clientID = post.getClientID();
                //post.getCompanyName();
                //documentSnapshot.get("name"); //same as above
                 //when you start new activity you can send id to retrive document or just the company name and image url
                Intent intent = new Intent(getActivity(), UserMessageActivity.class);
                intent.putExtra("ClientID", clientID);
                startActivity(intent);
            }
        });
        */
    }

    /*
    private void searchFilter(final String searchText){
        Query searchQuery = reference
                .orderBy("postDateTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ModelPost> newOptions = new FirestoreRecyclerOptions.Builder<ModelPost>()
                .setQuery(searchQuery, ModelPost.class)
                .build();


        //adapter.updateOptions(newOptions);
    }

*/

           @Override
           public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
               inflater.inflate(R.menu.search_menu, menu);
               MenuItem item = menu.findItem(R.id.action_search);
               SearchView search = (SearchView) MenuItemCompat.getActionView(item);
               search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                   @Override
                   public boolean onQueryTextSubmit(String query) {
                       Intent intent = new Intent(getActivity(), FilteredPost.class);
                       intent.putExtra("searchText", query);
                       startActivity(intent);
                       return false;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                       return false;
                   }
               });
               super.onCreateOptionsMenu(menu, inflater);
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

           // TODO: Rename method, update argument and hook method into UI event
           public void onButtonPressed(Uri uri) {
               if (mListener != null) {
                   mListener.onFragmentInteraction(uri);
               }
           }

           @Override
           public void onAttach(Context context) {
               super.onAttach(context);
           }

           @Override
           public void onDetach() {
               super.onDetach();
               mListener = null;
           }

           /**
            * This interface must be implemented by activities that contain this
            * fragment to allow an interaction in this fragment to be communicated
            * to the activity and potentially other fragments contained in that
            * activity.
            * <p>
            * See the Android Training lesson <a href=
            * "http://developer.android.com/training/basics/fragments/communicating.html"
            * >Communicating with Other Fragments</a> for more information.
            */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
