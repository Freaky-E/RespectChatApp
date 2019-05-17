package com.nonexistingdomainpleasedontsueme.respectchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateBtn;
    private Toolbar mToolBar;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mToolBar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabaseToken = FirebaseDatabase.getInstance().getReference().child("Users");

        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mDisplayName.getEditableText().toString().trim();
                String email = mEmail.getEditableText().toString().trim();
                String password = mPassword.getEditableText().toString().trim();


                register_user(display_name, email, password);


            }
        });


    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            if (task.isSuccessful()) {

                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("name", display_name);
                userMap.put("status", "I'm cool cause I'm on Respect Chat");
                userMap.put("image", "default");
                userMap.put("thumb image", "default");



                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String current_user_id = mAuth.getCurrentUser().getUid();

                            mUserDatabaseToken.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();


                                }
                            });

                         
                        }

                    }
                });





            } else {

                Toast.makeText(RegisterActivity.this, "Blow on it then try again", Toast.LENGTH_LONG).show();


            }

            }
        });

    }


}
