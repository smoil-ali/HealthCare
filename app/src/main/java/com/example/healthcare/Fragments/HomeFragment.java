package com.example.healthcare.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.Adapter.NewsFeedAdapter;
import com.example.healthcare.MainActivity;
import com.example.healthcare.Model.NewsFeedModel;
import com.example.healthcare.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView newsFeedRecyclerView;
    public List<NewsFeedModel> newsFeedModelList=new ArrayList<>();
    private NewsFeedAdapter newsFeedAdapter;
    private View view;
    private String POST = "Post";
    TextView alertMessageNewsFeed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        newsFeedRecyclerView = view.findViewById(R.id.postlist);
        newsFeedRecyclerView.hasFixedSize();
        newsFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alertMessageNewsFeed = view.findViewById(R.id.AlertMessageNewsFeed);
        FetchDataFromDatabase();
        return view;
    }

    public void FetchDataFromDatabase() {
        Log.i("city = ", MainActivity.CURRENT_CITY);
        if (MainActivity.CURRENT_CITY != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(POST);
            databaseReference.child(MainActivity.CURRENT_CITY).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        newsFeedModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                NewsFeedModel newsFeedModel = snapshot1.getValue(NewsFeedModel.class);
                                newsFeedModelList.add(newsFeedModel);
                            }
                        }
                        if (newsFeedModelList.size() > 0) {
                            alertMessageNewsFeed.setVisibility(View.GONE);
                            newsFeedAdapter = new NewsFeedAdapter(getContext(), newsFeedModelList);
                            newsFeedRecyclerView.setAdapter(newsFeedAdapter);
                        } else {
                            alertMessageNewsFeed.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Posts did not exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}
