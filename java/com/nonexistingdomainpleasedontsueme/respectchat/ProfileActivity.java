package com.nonexistingdomainpleasedontsueme.respectchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendRequest;
    private Button mDeclineBtn;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrent_User;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.profile_image_f);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileStatus = findViewById(R.id.profile_Status_f);
        mProfileFriendsCount = findViewById(R.id.profile_friendsCount);
        mProfileSendRequest = findViewById(R.id.send_f_request);
        mDeclineBtn = findViewById(R.id.profile_decline_btn);
        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        mCurrent_state = "not_friends";

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultpic).into(mProfileImage);

                //--------------FRIENDS LIST/REQUEST FEATURE--------------------------

                mFriendRequestDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (request_type.equals("received")) {

                                mCurrent_state = "request_received";
                                mProfileSendRequest.setText("Accept Friend request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);


                            } else if (request_type.equals("sent")){

                                mCurrent_state = "request_sent";
                                mProfileSendRequest.setText("Cancel friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);


                            }


                        } else {

                            mFriendDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends";
                                        mProfileSendRequest.setText("Unfriend");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);


                                    } else {

                                        mCurrent_state = "not_friends";
                                        mProfileSendRequest.setText("Send friend request");

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProfileSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendRequest.setEnabled(false);

                //----------------------------NOT FRIENDS STATE--------------------------

                if (mCurrent_state.equals("not_friends")) {

                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        mFriendRequestDatabase.child(user_id).child(mCurrent_User.getUid()).child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                HashMap<String, String> notificationData = new HashMap<>();
                                                notificationData.put("from", mCurrent_User.getUid());
                                                notificationData.put("type", "request");



                                                mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        
                                                        mProfileSendRequest.setEnabled(true);
                                                        mCurrent_state = "request_sent";
                                                        mProfileSendRequest.setText("Cancel Friend Request");

                                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                                        mDeclineBtn.setEnabled(false);

                                                    }
                                                });



                                                //Toast.makeText(ProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }


                                }
                            });

                }

                //---------------------------------CANCEL F REQUEST------------------------------

                if (mCurrent_state.equals("request_sent")) {


                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendRequestDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendRequest.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendRequest.setText("send friend request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);



                                }
                            });


                        }
                    });


                }
                //-----------------------REQUEST RECEIVED------------------------------------
                if (mCurrent_state.equals("request_received")) {

                    final String current_date = DateFormat.getDateInstance().format(new Date());

                    mFriendDatabase.child(mCurrent_User.getUid()).child(user_id).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrent_User.getUid()).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendRequestDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mProfileSendRequest.setEnabled(true);
                                                    mCurrent_state = "friends";
                                                    mProfileSendRequest.setText("Unfriend");

                                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                                    mDeclineBtn.setEnabled(false);



                                                }
                                            });


                                        }
                                    });


                                }
                            });


                        }
                    });



                }
                //------------------------------UNFRIEND----------------------------------------

                if (mCurrent_state.equals("friends")) {

                    mFriendDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mCurrent_state = "not_friends";
                                    mProfileSendRequest.setText("send friend request");

                                }
                            });

                        }
                    });



                }
            }
        });

    }
}
