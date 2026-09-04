package com.example.d308vacationscheduler;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
public abstract class VacationDatabase extends RoomDatabase {

    private static volatile VacationDatabase INSTANCE;

    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();

    static VacationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            VacationDatabase.class,
                            "vacation_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
