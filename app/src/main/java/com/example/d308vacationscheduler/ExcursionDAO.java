package com.example.d308vacationscheduler;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExcursionDAO {

    @Insert
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId ORDER BY id ASC")
    List<Excursion> getExcursionsForVacation(int vacationId);

    @Query("SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId")
    int getExcursionCountForVacation(int vacationId);
}
