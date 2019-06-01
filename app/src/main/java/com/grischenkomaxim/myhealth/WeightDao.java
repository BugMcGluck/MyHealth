package com.grischenkomaxim.myhealth;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WeightDao {
    //Получение всех записей
    @Query("SELECT * FROM weight order by time desc")
    List<Weight> getAll();

    //Получение последней записи
    @Query("SELECT * FROM (SELECT * FROM weight ORDER BY time DESC) LIMIT 1")
    Weight getLast();

    @Insert
    void insert(Weight weight);

    @Delete
    void delete(Weight weight);

    @Update
    void update(Weight weight);
}
