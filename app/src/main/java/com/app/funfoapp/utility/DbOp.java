package com.app.funfoapp.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.funfoapp.common.PostCodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by waheguru on 28/07/15.
 */
public class DbOp extends SQLiteOpenHelper {

    public DbOp(Context context) {
        super(context, "funfo_.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table session(id integer,uid text,userName text,emailId text,role text,logintype text,distance text)");
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", "-1");
        contentValues.put("uid", "-1");
        contentValues.put("role", "");
        contentValues.put("logintype", "");
        contentValues.put("distance", "");
        db.insert("session", null, contentValues);

        db.execSQL("create table postcode(id integer,postcode text,latitude text,longitude text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS session");
        db.execSQL("DROP TABLE IF EXISTS postcode");
        onCreate(db);
    }

    public void insertSessionId(String uid,String userName,String role,String logintype,String distance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("userName", userName);
        contentValues.put("role", role);
        contentValues.put("logintype", logintype);
        contentValues.put("distance", distance);
        db.update("session", contentValues, "id='-1'", null);
    }

//    public void insertCategoryId(String uid,String categoryName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("uid", uid);
//        contentValues.put("userName", userName);
//        contentValues.put("role", role);
//        db.update("session", contentValues, "id='-1'", null);
//    }

    public String getSessionId() {
        String uid = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from session where id='-1'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                uid = cursor.getString(cursor.getColumnIndex("uid"));
                cursor.moveToNext();
            }
            cursor.close();
        } else {

        }
        db.close();
        return uid;
    }
    public String getlogintype() {
        String phono = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from session where id='-1'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                phono = cursor.getString(cursor.getColumnIndex("logintype"));
                cursor.moveToNext();
            }
            cursor.close();
        } else {

        }
        db.close();
        return phono;
    }
    public String getRole() {
        String code = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from session where id='-1'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                code = cursor.getString(cursor.getColumnIndex("role"));
                cursor.moveToNext();
            }
            cursor.close();
        } else {

        }
        db.close();
        return code;
    }

    public String getDistance() {
        String code = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from session where id='-1'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                code = cursor.getString(cursor.getColumnIndex("distance"));
                cursor.moveToNext();
            }
            cursor.close();
        } else {

        }
        db.close();
        return code;
    }

    public String getlatitude(String postcode) {
        String code = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from postcode where postcode='"+postcode+"'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                code = cursor.getString(cursor.getColumnIndex("latitude"));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
        return code;
    }
    public String getlongitude(String postcode) {
        String code = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from postcode where postcode='"+postcode+"'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                code = cursor.getString(cursor.getColumnIndex("longitude"));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
        return code;
    }

    public boolean getPostCode(String postcode) {
        String code = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from postcode where postcode='"+postcode+"'", null);
        if (cursor != null && cursor.getCount()>0) {
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public void insertPostCode(String postcode,String latitude,String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("postcode", postcode);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        db.insert("postcode", null, contentValues);
    }
    public ArrayList<PostCodeList> getpostcode(Context context) {
        ArrayList<PostCodeList> arr = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query="";
        query="SELECT *  FROM postcode";
        //Log.e("tag","query "+query);
        Cursor cursor = db
                .rawQuery(query, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                HashMap<Object,String> hm=new HashMap<>();
                hm.put("postcode", cursor.getString(cursor.getColumnIndex("postcode")));
                hm.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                hm.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                PostCodeList cp=new PostCodeList(context,hm);
                arr.add(cp);
                // Collections.sort(arr, (Comparator<? super HashMap<String, ?>>) new MapComparator("CardCount"));
            }
            cursor.close();
        } else {

        }
        db.close();
        return arr;
    }
    public ArrayList<String> getpostcode1(Context context) {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query="";
        query="SELECT *  FROM postcode";
        //Log.e("tag","query "+query);
        Cursor cursor = db
                .rawQuery(query, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                HashMap<Object,String> hm=new HashMap<>();
                hm.put("postcode", cursor.getString(cursor.getColumnIndex("postcode")));
                hm.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                hm.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                //PostCodeList cp=new PostCodeList(context,hm);
                arr.add(cursor.getString(cursor.getColumnIndex("postcode")));
                // Collections.sort(arr, (Comparator<? super HashMap<String, ?>>) new MapComparator("CardCount"));
            }
            cursor.close();
        } else {

        }
        db.close();
        return arr;
    }
}
