package com.asier.nemergenttest.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import com.asier.nemergenttest.models.Picture;


public class DBController extends SQLiteOpenHelper {
    private static final String DB_NAME = "pic-library";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "pics";
    private static final String PIC_ID= "id";
    private static final String PIC_DATE = "date";
    private static final String PIC_LOCATION = "location";
    private static final String PIC_ROUTE = "route";

    public DBController(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_NAME + " ("
                + PIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PIC_DATE + " DATE, "
                + PIC_LOCATION + " TEXT, "
                + PIC_ROUTE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertPicture(String timestamp, String location, String route) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" + PIC_LOCATION + ", " + PIC_DATE + ", " + PIC_ROUTE + ") VALUES(?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(sql);

        stmt.bindString(1, timestamp);
        stmt.bindString(2, location);
        stmt.bindString(3, route);
        stmt.executeInsert();
        db.close();
    }

    public void removePicture(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<Picture> getPictures() {
        ArrayList<Picture> result = new ArrayList<Picture>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                PIC_ID,
                PIC_DATE,
                PIC_LOCATION,
                PIC_ROUTE,
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null,
                null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDate = LocalDateTime.parse(cursor.getString(2), formatter);
            Picture pic = new Picture(
                    cursor.getInt(0),
                    Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant()),
                    cursor.getString(1),
                    cursor.getString(3)
            );
            result.add(pic);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

}
