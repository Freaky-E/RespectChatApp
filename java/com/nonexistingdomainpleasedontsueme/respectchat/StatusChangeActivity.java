package com.nonexistingdomainpleasedontsueme.respectchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusChangeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mStatus;
    private Button mSaveBtn;

    private DatabaseReference mStatusDataBase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);



        mToolbar = findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Status Change");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mStatus = findViewById(R.id.status_input);
        mSaveBtn = findViewById(R.id.status_save_btn);


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = mStatus.getEditableText().toString().trim();

                mStatusDataBase.child("status").setValue(status);

                Intent backIntent = new Intent(StatusChangeActivity.this, SettingsActivity.class);
                startActivity(backIntent);
                finish();

            }
        });


    }
}
