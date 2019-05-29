package com.example.emerald;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private RecyclerView mRequestsList;

    private DatabaseReference mUsersDatabse;
    private DatabaseReference mRequestsDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private View mMainView;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestsList = mMainView.findViewById(R.id.requests_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mRequestsDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Friend_requests").child(mCurrentUserId);

        mRequestsDatabase.keepSynced(true);

        mUsersDatabse = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabse.keepSynced(true);

        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        Query requestsQuery = mRequestsDatabase.orderByChild("request_type").equalTo("received");

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> firebaseReqAdapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(
                Requests.class,
                R.layout.users_single_layout,
                RequestsViewHolder.class,
                requestsQuery
        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, Requests model, int position) {

                final String listUserId = getRef(position).getKey();

                mUsersDatabse.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userImage = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);

                        }

                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userImage, getContext());
                        viewHolder.setUserStatus();

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("userId", listUserId);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        mRequestsList.setAdapter(firebaseReqAdapter);
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name) {

            TextView userName = mView.findViewById(R.id.user_single_name);
            userName.setText(name);

        }

        public void setUserImage(String image, Context context) {

            CircleImageView userImage = mView.findViewById(R.id.user_single_img);
            Picasso.with(context).load(image).placeholder(R.drawable.hi).into(userImage);

        }

        public void setUserStatus() {

            TextView userStatus = mView.findViewById(R.id.user_single_status);
            userStatus.setText("Wants to be a Friend");

        }

        public void setUserOnline(String onlineStatus) {

            ImageView userOnline = mView.findViewById(R.id.user_single_online_icon);

            if (onlineStatus.equals("true")) {
                userOnline.setVisibility(View.VISIBLE);
            } else {
                userOnline.setVisibility(View.INVISIBLE);
            }

        }

    }
}
