package com.grischenkomaxim.myhealth;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WaterDao {
    //Получение всех записей
    @Query("SELECT * FROM water order by time desc")
    List<Water> getAll();

    //Получение последней записи
    @Query("SELECT * FROM (SELECT * FROM water ORDER BY time DESC) LIMIT 1")
    Water getLast();

    @Insert
    void insert(Water water);

    @Delete
    void delete(Water water);

    @Update
    void update(Water water);
}
