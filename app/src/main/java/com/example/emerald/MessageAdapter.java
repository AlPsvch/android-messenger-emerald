package com.example.emerald;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserDatabase;


    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_single_layout, viewGroup, false);

        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView sendingTime;


        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);
            displayName = view.findViewById(R.id.name_text_layout);
            sendingTime = view.findViewById(R.id.time_text_layout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

        Messages message = mMessageList.get(i);

        String fromUser = message.getFrom();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                messageViewHolder.displayName.setText(name);

                Picasso.with(messageViewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.hi).into(messageViewHolder.profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        long messageTime = message.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageTime);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        String messageTimeString = (hour >= 10 ? hour : ("0" + hour)) + ":" + (minute >= 10 ? minute : ("0" + minute));


        messageViewHolder.messageText.setText(message.getMessage());
        messageViewHolder.sendingTime.setText(messageTimeString);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
