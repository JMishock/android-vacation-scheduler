package com.example.d308vacationscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcursionDetailActivity extends AppCompatActivity {

    private EditText editExcursionTitle;
    private EditText editExcursionDate;

    private Button buttonSaveExcursion;
    private Button buttonDeleteExcursion;
    private Button buttonSetExcursionAlert;

    private VacationRepository vacationRepository;

    private Excursion currentExcursion;
    private Vacation parentVacation;

    private int vacationId;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_detail);

        editExcursionTitle = findViewById(R.id.editExcursionTitle);
        editExcursionDate = findViewById(R.id.editExcursionDate);

        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);
        buttonSetExcursionAlert = findViewById(R.id.buttonSetExcursionAlert);

        vacationRepository = new VacationRepository(getApplication());

        dateFormat.setLenient(false);

        vacationId = getIntent().getIntExtra("vacationId", -1);
        int excursionId = getIntent().getIntExtra("excursionId", -1);

        if (vacationId == -1) {
            Toast.makeText(
                    this,
                    "Vacation could not be identified.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadParentVacation();

        editExcursionDate.setOnClickListener(v ->
                showDatePicker(editExcursionDate)
        );

        buttonSaveExcursion.setOnClickListener(v ->
                saveExcursion()
        );

        buttonDeleteExcursion.setOnClickListener(v ->
                deleteExcursion()
        );

        buttonSetExcursionAlert.setOnClickListener(v ->
                setExcursionAlert()
        );

        if (excursionId == -1) {
            buttonDeleteExcursion.setEnabled(false);
            buttonSetExcursionAlert.setEnabled(false);
        } else {
            buttonDeleteExcursion.setEnabled(true);
            buttonSetExcursionAlert.setEnabled(true);
            loadExcursion(excursionId);
        }
    }

    private void loadParentVacation() {
        executorService.execute(() -> {

            parentVacation =
                    vacationRepository.getVacationById(vacationId);

            if (parentVacation == null) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            ExcursionDetailActivity.this,
                            "Vacation could not be found.",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });
            }
        });
    }

    private void loadExcursion(int excursionId) {
        executorService.execute(() -> {

            for (Excursion excursion :
                    vacationRepository.getExcursionsForVacation(vacationId)) {

                if (excursion.getId() == excursionId) {
                    currentExcursion = excursion;
                    break;
                }
            }

            runOnUiThread(() -> {

                if (currentExcursion != null) {

                    editExcursionTitle.setText(
                            currentExcursion.getTitle()
                    );

                    editExcursionDate.setText(
                            currentExcursion.getDate()
                    );

                } else {

                    Toast.makeText(
                            ExcursionDetailActivity.this,
                            "Excursion could not be found.",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                }
            });
        });
    }

    private void showDatePicker(EditText targetField) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            String selectedDate =
                                    String.format(
                                            Locale.US,
                                            "%02d/%02d/%04d",
                                            month + 1,
                                            dayOfMonth,
                                            year
                                    );

                            targetField.setText(selectedDate);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.show();
    }

    private void saveExcursion() {

        String title =
                editExcursionTitle.getText().toString().trim();

        String date =
                editExcursionDate.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please complete all excursion fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (parentVacation == null) {

            Toast.makeText(
                    this,
                    "Vacation information is not available yet.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Date excursionDate =
                    dateFormat.parse(date);

            Date vacationStart =
                    dateFormat.parse(parentVacation.getStartDate());

            Date vacationEnd =
                    dateFormat.parse(parentVacation.getEndDate());

            if (excursionDate == null
                    || vacationStart == null
                    || vacationEnd == null) {

                Toast.makeText(
                        this,
                        "Please enter a valid excursion date.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (excursionDate.before(vacationStart)
                    || excursionDate.after(vacationEnd)) {

                Toast.makeText(
                        this,
                        "Excursion date must fall within the vacation dates.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

        } catch (ParseException e) {

            Toast.makeText(
                    this,
                    "Excursion date must use MM/dd/yyyy format.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (currentExcursion == null) {

            Excursion newExcursion =
                    new Excursion(
                            vacationId,
                            title,
                            date
                    );

            vacationRepository.insertExcursion(newExcursion);

            Toast.makeText(
                    this,
                    "Excursion saved.",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            currentExcursion.setTitle(title);
            currentExcursion.setDate(date);

            vacationRepository.updateExcursion(currentExcursion);

            Toast.makeText(
                    this,
                    "Excursion updated.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    private void setExcursionAlert() {

        if (currentExcursion == null) {

            Toast.makeText(
                    this,
                    "Save the excursion before setting an alert.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Date alertDate =
                    dateFormat.parse(currentExcursion.getDate());

            if (alertDate == null) {

                Toast.makeText(
                        this,
                        "Excursion date could not be read.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alertDate);

            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Intent intent =
                    new Intent(
                            this,
                            VacationAlertReceiver.class
                    );

            intent.putExtra(
                    "title",
                    currentExcursion.getTitle()
            );

            intent.putExtra(
                    "message",
                    "Excursion today: "
                            + currentExcursion.getTitle()
            );

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            this,
                            3000 + currentExcursion.getId(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE
                    );

            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(
                            Context.ALARM_SERVICE
                    );

            if (alarmManager != null) {

                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );

                Toast.makeText(
                        this,
                        "Excursion alert set.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (ParseException e) {

            Toast.makeText(
                    this,
                    "Excursion date must use MM/dd/yyyy format.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void deleteExcursion() {

        if (currentExcursion == null) {
            return;
        }

        vacationRepository.deleteExcursion(currentExcursion);

        Toast.makeText(
                this,
                "Excursion deleted.",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}