package com.grischenkomaxim.myhealth;

import java.io.Serializable;
import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BloodPressure implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "valueSystolic")
    private int valueSystolic;

    @ColumnInfo(name = "valueDiastolic")
    private int valueDiastolic;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "update_time")
    private Long update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValueSystolic() {
        return valueSystolic;
    }

    public void setValueSystolic(int valueSystolic) {
        this.valueSystolic = valueSystolic;
    }

    public int getValueDiastolic() {
        return valueDiastolic;
    }

    public void setValueDiastolic(int valueDiastolic) {
        this.valueDiastolic = valueDiastolic;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public Date getDateTime() {
        return new Date(time);
    }

    public void setTime(Date time) {
        this.time = time.getTime();
    }

    public Date getDateUpdate_time() {
        return new Date(update_time);
    }

    public void setUpdate_time() {
        this.update_time = new Date().getTime();
    }
}
