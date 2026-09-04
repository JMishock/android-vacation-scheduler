package com.example.d308vacationscheduler;

import android.app.Application;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationRepository {

    private final VacationDAO vacationDAO;
    private final ExcursionDAO excursionDAO;
    private final ExecutorService executorService;

    public VacationRepository(Application application) {
        VacationDatabase db = VacationDatabase.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Vacation vacation) {
        executorService.execute(() -> vacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        executorService.execute(() -> vacationDAO.update(vacation));
    }

    public void delete(Vacation vacation) {
        executorService.execute(() -> vacationDAO.delete(vacation));
    }

    public List<Vacation> getAllVacations() {
        return vacationDAO.getAllVacations();
    }

    public Vacation getVacationById(int vacationId) {
        return vacationDAO.getVacationById(vacationId);
    }

    public void insertExcursion(Excursion excursion) {
        executorService.execute(() -> excursionDAO.insert(excursion));
    }

    public void updateExcursion(Excursion excursion) {
        executorService.execute(() -> excursionDAO.update(excursion));
    }

    public void deleteExcursion(Excursion excursion) {
        executorService.execute(() -> excursionDAO.delete(excursion));
    }

    public List<Excursion> getExcursionsForVacation(int vacationId) {
        return excursionDAO.getExcursionsForVacation(vacationId);
    }

    public int getExcursionCountForVacation(int vacationId) {
        return excursionDAO.getExcursionCountForVacation(vacationId);
    }
}