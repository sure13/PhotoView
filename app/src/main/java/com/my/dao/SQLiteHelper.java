package com.my.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper( Context context) {
        super(context, Dao.DATABASE_NAME,null, Dao.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Dao.TABLE_NAME + " ("
                + Dao.ID + " INTEGER PRIMARY KEY,"
                + Dao.PATH + " VARCHAR(100)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Dao.TABLE_NAME);
        onCreate(db);
    }
}
