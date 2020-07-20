package com.example.tovisit_ram_c0779370_android;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final  String DATABASE_NAME = "LocationDatabase";
    private static final int DATABASE_VERSION = 1;
    private static  final  String TABLE_NAME = "location";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LAT ="lat";
    private static final String COLUMN_LONG = "lng";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_DATE = "date";

    private static final String COLUMN_VISITED = "visited";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + "(" +
                COLUMN_ID + " Integer not null constraint loaction_pk primary key autoincrement," +
                COLUMN_LAT + " double not null, " +
                COLUMN_LONG + " double not null, " +
                COLUMN_ADDRESS + " varchar(200), " +
                COLUMN_DATE + " varchar(200) not null, " +
                COLUMN_VISITED + " varchar(200) not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + TABLE_NAME + ";" ;
        db.execSQL(sql);
        onCreate(db);
    }

    boolean addlocation(double lat, double lng, String address, String date, String visited) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //we need to define a content Values instances
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, String.valueOf(lat));
        cv.put(COLUMN_LONG, String.valueOf(lng));
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_VISITED, visited);
        return  sqLiteDatabase.insert(TABLE_NAME,null, cv) != -1;

    }

    Cursor getAllLocation(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);

    }

    boolean updateLoaction(int id, double lat, double lng, String address, String date,String visited) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, String.valueOf(lat));
        cv.put(COLUMN_LONG, String.valueOf(lng));
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_VISITED, visited);

        Log.e("updateLoaction", visited);
        return  sqLiteDatabase.update(TABLE_NAME, cv,COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
    boolean deleteLocation(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return  sqLiteDatabase.delete(TABLE_NAME,COLUMN_ID + "=?" , new String[]{ String.valueOf(id)}) > 0;

    }
}
