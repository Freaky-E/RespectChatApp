package com.nonexistingdomainpleasedontsueme.respectchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn = findViewById(R.id.start_reg_btn);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);


            }
        });

        mLogBtn = findViewById(R.id.start_log_btn);

        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent log_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(log_intent);

            }
        });

    }
}
