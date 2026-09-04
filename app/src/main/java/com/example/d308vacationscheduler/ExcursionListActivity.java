package com.example.d308vacationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcursionListActivity extends AppCompatActivity {

    private ListView excursionListView;
    private Button buttonAddExcursion;

    private VacationRepository vacationRepository;

    private final List<Excursion> excursions = new ArrayList<>();
    private ArrayAdapter<String> excursionAdapter;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        excursionListView = findViewById(R.id.excursionListView);
        buttonAddExcursion = findViewById(R.id.buttonAddExcursion);

        vacationRepository = new VacationRepository(getApplication());

        vacationId = getIntent().getIntExtra("vacationId", -1);

        if (vacationId == -1) {
            Toast.makeText(
                    this,
                    "Vacation could not be identified.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        excursionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>()
        );

        excursionListView.setAdapter(excursionAdapter);

        buttonAddExcursion.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ExcursionListActivity.this,
                    ExcursionDetailActivity.class
            );

            intent.putExtra("vacationId", vacationId);

            startActivity(intent);
        });

        excursionListView.setOnItemClickListener((parent, view, position, id) -> {
            Excursion selectedExcursion = excursions.get(position);

            Intent intent = new Intent(
                    ExcursionListActivity.this,
                    ExcursionDetailActivity.class
            );

            intent.putExtra("vacationId", vacationId);
            intent.putExtra("excursionId", selectedExcursion.getId());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExcursions();
    }

    private void loadExcursions() {
        executorService.execute(() -> {

            List<Excursion> databaseExcursions =
                    vacationRepository.getExcursionsForVacation(vacationId);

            runOnUiThread(() -> {

                excursions.clear();
                excursions.addAll(databaseExcursions);

                List<String> excursionDetails = new ArrayList<>();

                for (Excursion excursion : excursions) {

                    String displayText =
                            excursion.getTitle()
                                    + "\n"
                                    + excursion.getDate();

                    excursionDetails.add(displayText);
                }

                excursionAdapter.clear();
                excursionAdapter.addAll(excursionDetails);
                excursionAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}