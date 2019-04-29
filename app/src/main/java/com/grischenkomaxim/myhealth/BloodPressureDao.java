package com.grischenkomaxim.myhealth;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BloodPressureDao {
    //Получение всех записей
    @Query("SELECT * FROM bloodpressure")
    List<BloodPressure> getAll();

    //Получение последней записи
    @Query("SELECT * FROM (SELECT * FROM bloodpressure ORDER BY time DESC) LIMIT 1")
    BloodPressure getLast();

    @Insert
    void insert(BloodPressure bloodPressure);

    @Delete
    void delete(BloodPressure bloodPressure);

    @Update
    void update(BloodPressure bloodPressure);
}
