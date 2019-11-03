package com.egyptrefaat.supporting.supportingonline;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WorkWithActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
