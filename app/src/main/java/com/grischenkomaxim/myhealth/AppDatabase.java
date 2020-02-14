package com.grischenkomaxim.myhealth;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Weight.class, BloodPressure.class, Water.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract WeightDao weightDao();

    public abstract BloodPressureDao bloodPressureDao();

    public abstract WaterDao waterDao();

}
