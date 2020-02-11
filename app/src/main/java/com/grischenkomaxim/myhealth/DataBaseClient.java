package com.grischenkomaxim.myhealth;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DataBaseClient {

    private Context mCtx;
    private static DataBaseClient mInstance;

    //our app database object
    private AppDatabase appDatabase;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Water` (`id` INTEGER NOT NULL,`value` INTEGER NOT NULL, `time` INTEGER, `update_time` INTEGER, PRIMARY KEY(`id`))");
        }
    };


    private DataBaseClient(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyDB is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "MyDB").addMigrations(MIGRATION_1_2).build();
    }

    public static synchronized DataBaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DataBaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
