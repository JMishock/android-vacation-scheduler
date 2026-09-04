package com.example.d308vacationscheduler;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationDetailActivity extends AppCompatActivity {

    private EditText editVacationTitle;
    private EditText editHotel;
    private EditText editStartDate;
    private EditText editEndDate;

    private Button buttonSaveVacation;
    private Button buttonSetVacationAlerts;
    private Button buttonShareVacation;
    private Button buttonViewExcursions;
    private Button buttonDeleteVacation;

    private VacationRepository vacationRepository;

    private Vacation currentVacation;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        editVacationTitle = findViewById(R.id.editVacationTitle);
        editHotel = findViewById(R.id.editHotel);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);

        buttonSaveVacation = findViewById(R.id.buttonSaveVacation);
        buttonSetVacationAlerts =
                findViewById(R.id.buttonSetVacationAlerts);
        buttonShareVacation =
                findViewById(R.id.buttonShareVacation);
        buttonViewExcursions =
                findViewById(R.id.buttonViewExcursions);
        buttonDeleteVacation =
                findViewById(R.id.buttonDeleteVacation);

        vacationRepository =
                new VacationRepository(getApplication());

        dateFormat.setLenient(false);

        requestNotificationPermission();

        editStartDate.setOnClickListener(v ->
                showDatePicker(editStartDate)
        );

        editEndDate.setOnClickListener(v ->
                showDatePicker(editEndDate)
        );

        buttonSaveVacation.setOnClickListener(v ->
                saveVacation()
        );

        buttonSetVacationAlerts.setOnClickListener(v ->
                setVacationAlerts()
        );

        buttonShareVacation.setOnClickListener(v ->
                shareVacation()
        );

        buttonViewExcursions.setOnClickListener(v ->
                viewExcursions()
        );

        buttonDeleteVacation.setOnClickListener(v ->
                deleteVacation()
        );

        int vacationId =
                getIntent().getIntExtra("vacationId", -1);

        if (vacationId == -1) {

            buttonDeleteVacation.setEnabled(false);
            buttonViewExcursions.setEnabled(false);
            buttonSetVacationAlerts.setEnabled(false);
            buttonShareVacation.setEnabled(false);

        } else {

            buttonDeleteVacation.setEnabled(true);
            buttonViewExcursions.setEnabled(true);
            buttonSetVacationAlerts.setEnabled(true);
            buttonShareVacation.setEnabled(true);

            loadVacation(vacationId);
        }
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void loadVacation(int vacationId) {

        executorService.execute(() -> {

            Vacation vacation =
                    vacationRepository.getVacationById(vacationId);

            runOnUiThread(() -> {

                if (vacation != null) {

                    currentVacation = vacation;

                    editVacationTitle.setText(
                            vacation.getTitle()
                    );

                    editHotel.setText(
                            vacation.getHotel()
                    );

                    editStartDate.setText(
                            vacation.getStartDate()
                    );

                    editEndDate.setText(
                            vacation.getEndDate()
                    );

                } else {

                    Toast.makeText(
                            VacationDetailActivity.this,
                            "Vacation could not be found.",
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

    private void saveVacation() {

        String title =
                editVacationTitle.getText().toString().trim();

        String hotel =
                editHotel.getText().toString().trim();

        String startDate =
                editStartDate.getText().toString().trim();

        String endDate =
                editEndDate.getText().toString().trim();

        if (title.isEmpty()
                || hotel.isEmpty()
                || startDate.isEmpty()
                || endDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please complete all vacation fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Date parsedStartDate =
                    dateFormat.parse(startDate);

            Date parsedEndDate =
                    dateFormat.parse(endDate);

            if (parsedStartDate == null
                    || parsedEndDate == null) {

                Toast.makeText(
                        this,
                        "Please enter valid vacation dates.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!parsedEndDate.after(parsedStartDate)) {

                Toast.makeText(
                        this,
                        "Vacation end date must be after the start date.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

        } catch (ParseException e) {

            Toast.makeText(
                    this,
                    "Dates must use MM/dd/yyyy format.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (currentVacation == null) {

            Vacation newVacation =
                    new Vacation(
                            title,
                            hotel,
                            startDate,
                            endDate
                    );

            vacationRepository.insert(newVacation);

            Toast.makeText(
                    this,
                    "Vacation saved.",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            currentVacation.setTitle(title);
            currentVacation.setHotel(hotel);
            currentVacation.setStartDate(startDate);
            currentVacation.setEndDate(endDate);

            vacationRepository.update(currentVacation);

            Toast.makeText(
                    this,
                    "Vacation updated.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    private void setVacationAlerts() {

        if (currentVacation == null) {

            Toast.makeText(
                    this,
                    "Save the vacation before setting alerts.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Date startDate =
                    dateFormat.parse(
                            currentVacation.getStartDate()
                    );

            Date endDate =
                    dateFormat.parse(
                            currentVacation.getEndDate()
                    );

            if (startDate == null || endDate == null) {

                Toast.makeText(
                        this,
                        "Vacation dates could not be read.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            scheduleAlert(
                    startDate,
                    currentVacation.getTitle(),
                    currentVacation.getTitle()
                            + " is starting today.",
                    currentVacation.getId() * 2
            );

            scheduleAlert(
                    endDate,
                    currentVacation.getTitle(),
                    currentVacation.getTitle()
                            + " is ending today.",
                    currentVacation.getId() * 2 + 1
            );

            Toast.makeText(
                    this,
                    "Vacation start and end alerts have been set.",
                    Toast.LENGTH_LONG
            ).show();

        } catch (ParseException e) {

            Toast.makeText(
                    this,
                    "Unable to set alerts for these dates.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void scheduleAlert(
            Date alertDate,
            String title,
            String message,
            int requestCode
    ) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alertDate);

        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent =
                new Intent(
                        VacationDetailActivity.this,
                        VacationAlertReceiver.class
                );

        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(
                        Context.ALARM_SERVICE
                );

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    private void shareVacation() {

        if (currentVacation == null) {

            Toast.makeText(
                    this,
                    "Save the vacation before sharing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String vacationDetails =
                "Vacation: "
                        + currentVacation.getTitle()
                        + "\n"
                        + "Hotel / Accommodation: "
                        + currentVacation.getHotel()
                        + "\n"
                        + "Start Date: "
                        + currentVacation.getStartDate()
                        + "\n"
                        + "End Date: "
                        + currentVacation.getEndDate();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Vacation Details: "
                        + currentVacation.getTitle()
        );

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                vacationDetails
        );

        startActivity(
                Intent.createChooser(
                        shareIntent,
                        "Share Vacation Details"
                )
        );
    }

    private void viewExcursions() {

        if (currentVacation == null) {

            Toast.makeText(
                    this,
                    "Save the vacation before adding excursions.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        VacationDetailActivity.this,
                        ExcursionListActivity.class
                );

        intent.putExtra(
                "vacationId",
                currentVacation.getId()
        );

        startActivity(intent);
    }

    private void deleteVacation() {

        if (currentVacation == null) {
            return;
        }

        executorService.execute(() -> {

            int excursionCount =
                    vacationRepository
                            .getExcursionCountForVacation(
                                    currentVacation.getId()
                            );

            if (excursionCount > 0) {

                runOnUiThread(() ->
                        Toast.makeText(
                                VacationDetailActivity.this,
                                "Cannot delete this vacation because excursions are associated with it.",
                                Toast.LENGTH_LONG
                        ).show()
                );

            } else {

                vacationRepository.delete(currentVacation);

                runOnUiThread(() -> {

                    Toast.makeText(
                            VacationDetailActivity.this,
                            "Vacation deleted.",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}