package com.example.d308vacationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationListActivity extends AppCompatActivity {

    private ListView vacationListView;
    private Button buttonAddVacation;

    private VacationRepository vacationRepository;
    private final List<Vacation> vacations = new ArrayList<>();

    private ArrayAdapter<String> vacationAdapter;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        vacationListView = findViewById(R.id.vacationListView);
        buttonAddVacation = findViewById(R.id.buttonAddVacation);

        vacationRepository = new VacationRepository(getApplication());

        vacationAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>()
        );

        vacationListView.setAdapter(vacationAdapter);

        buttonAddVacation.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VacationListActivity.this,
                    VacationDetailActivity.class
            );
            startActivity(intent);
        });

        vacationListView.setOnItemClickListener((parent, view, position, id) -> {
            Vacation selectedVacation = vacations.get(position);

            Intent intent = new Intent(
                    VacationListActivity.this,
                    VacationDetailActivity.class
            );

            intent.putExtra("vacationId", selectedVacation.getId());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVacations();
    }

    private void loadVacations() {
        executorService.execute(() -> {

            List<Vacation> databaseVacations =
                    vacationRepository.getAllVacations();

            runOnUiThread(() -> {

                vacations.clear();
                vacations.addAll(databaseVacations);

                List<String> vacationNames = new ArrayList<>();

                for (Vacation vacation : vacations) {
                    vacationNames.add(vacation.getTitle());
                }

                vacationAdapter.clear();
                vacationAdapter.addAll(vacationNames);
                vacationAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}