package com.grischenkomaxim.myhealth;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Water implements Serializable {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public Date getDateTime() {
        return new Date(time);
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setTime(Date time) {
        this.time = time.getTime();
    }

    public Long getUpdate_time() {
        return update_time;
    }
    public Date getDateUpdate_time() {
        return new Date(update_time);
    }
    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public void setUpdate_time() {
        this.update_time = new Date().getTime();
    }
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "value")
    private int value;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "update_time")
    private Long update_time;
}
