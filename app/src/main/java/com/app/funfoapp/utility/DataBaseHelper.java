package com.app.funfoapp.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper {
    private Context mycontext;
    private String DB_PATH;
    private static String DB_NAME = "funfo.sqlite";//the extension may be .sqlite or .db
    public SQLiteDatabase myDataBase;

    private void copyDB() throws IOException{
        InputStream myInput = mycontext.getAssets().open("mydata.sqlite");
        String outFileName = DB_PATH+DB_NAME;
        // if the path doesn't exist first, create it
        File f = new File(DB_PATH);
        if (!f.exists())
        f.mkdir();

        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        System.out.println("copying..... Database from assset");
    }

    public DataBaseHelper(Context context) throws IOException, SQLException {
        super(context, DB_NAME, null, 1);
        this.mycontext = context;
        // DB_PATH = mycontext.getApplicationContext().getPackageName()+"/databases/";
        DB_PATH = "/data/data/"
                + mycontext.getApplicationContext().getPackageName()
                + "/databases/";
        boolean dbexist = checkdatabase();
        if (!dbexist) {
            copyDB();
        } else {
            //Toast.makeText(context,"database exist ",Toast.LENGTH_SHORT).show();
            opendatabase();
        }
    }

    private boolean checkdatabase() {
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist catch block");
        }

        return checkdb;
    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public ArrayList<String> getPostCodes() {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query="";
        query="SELECT *  FROM postcodes";
        //Log.e("tag","query "+query);
        Cursor cursor = db
                .rawQuery(query, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
//                HashMap<String,Object> hm=new HashMap<>();
//                hm.put("Postcode", cursor.getString(cursor.getColumnIndex("Postcode")));
//                hm.put("Latitude", cursor.getString(cursor.getColumnIndex("Latitude")));
//                hm.put("Longitude", cursor.getString(cursor.getColumnIndex("Longitude")));
                arr.add(cursor.getString(cursor.getColumnIndex("Postcode")));
                // Collections.sort(arr, (Comparator<? super HashMap<String, ?>>) new MapComparator("CardCount"));
            }
            cursor.close();
        } else {

        }
        db.close();
        return arr;
    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + "postcodes";
        db.execSQL(sql);
        onCreate(db);
    }

    public String getPostCodeLat(String postcode) {
        String lat = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from postcodes where Postcode='"+postcode+"'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lat=cursor.getString(cursor.getColumnIndex("Latitude"));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
        return lat;
    }

    public String getPostCodeLng(String postcode) {
        String lng = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from postcodes where Postcode='"+postcode+"'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lng=cursor.getString(cursor.getColumnIndex("Longitude"));
                cursor.moveToNext();
            }
            cursor.close();
        }

        db.close();
        return lng;
    }

    public boolean checkPostCodeAvailabiity(String postcode) {
        boolean code = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db
                .rawQuery("select * from postcodes where postcode='"+postcode+"'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                code= true;
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
        return code;
    }
}