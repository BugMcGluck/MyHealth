package com.grischenkomaxim.myhealth;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Weight.class, BloodPressure.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract WeightDao weightDao();
    public abstract BloodPressureDao bloodPressureDao();
}
