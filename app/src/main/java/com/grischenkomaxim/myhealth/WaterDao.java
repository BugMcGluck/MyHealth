package com.grischenkomaxim.myhealth;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WaterDao {
    //Получение всех записей
    @Query("SELECT * FROM water order by time desc")
    List<Water> getAll();

    //Получение последней записи
    @Query("SELECT * FROM (SELECT * FROM water ORDER BY time DESC) LIMIT 1")
    Water getLast();

    //Получение суммарного значения за текущую дату
    @Query("SELECT sum(value) FROM water where time > :date")
    int getDayValue (Long date);

    @Insert
    void insert(Water water);

    @Delete
    void delete(Water water);

    @Update
    void update(Water water);
}
