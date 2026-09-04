package com.example.d308vacationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonViewVacations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonViewVacations = findViewById(R.id.buttonViewVacations);

        buttonViewVacations.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    VacationListActivity.class
            );

            startActivity(intent);
        });
    }
}