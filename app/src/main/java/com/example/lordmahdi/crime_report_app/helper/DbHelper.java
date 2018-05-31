package com.example.lordmahdi.crime_report_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "store_db";
    private static final int db_version = 1;
    private Context context;
    public DbHelper(Context context){
        super(context,DB_NAME,null,db_version);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //String reporterTable = "CREATE TABLE IF NOT EXISTS reporter(_id INTEGER PRIMARY KEY AUTOINCREMENT," + "fname TEXT,lname TEXT,phone TEXT,location_id INTEGER)";


        String userInfo = "CREATE TABLE IF NOT EXISTS User(_id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT,name TEXT,lname TEXT,password TEXT,phone TEXT,image TEXT)";

        try{
            db.execSQL(userInfo);
        }catch (Exception e){
            Log.e("table err: ",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public long insertTo(String tableName,ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();
        //TODO: return the insert row id or -1 if an error accursed
        long rowId=-1;
        try{
            rowId = db.insert(tableName,null,values);
        }catch (Exception e){ rowId = -1;}
        finally {
            return rowId;
        }
    }



    public Cursor getAlldataFrom(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + tableName, null);
    }



    public Cursor getAlldataAlternativelyFrom(String tableName){
        Cursor allData = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            allData =  db.query(tableName, null, null, null, null, null, null);
        }catch (Exception e){}
        return allData;
    }



    public Cursor getAlldataReverce(String tableName,String phoneNumber){
        Cursor allData = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            allData =  db.query(tableName,null,"phone=?",new String[]{phoneNumber},null, null,"_id DESC");
        }catch (Exception e){}
        return allData;
    }




    public Cursor getDataFrom(String tableName,String[]columnName){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            //TODO: return all selected data with type cursor
            Cursor data = db.query(tableName, columnName, null, null, null, null, null);
            //db.close();
            return data;
        }catch (Exception e){
            //Toast.makeText(context, "errrrrrrrrrrrrrrrrrrrrrrrrrrrr", Toast.LENGTH_SHORT).show();
            return null;
        }
    }





    public Cursor getDataFromWithId(String tableName,String[]columnName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = null;
        try {
           data.moveToFirst();
           int id1 = data.getInt(data.getColumnIndex("_id"));
           data = db.query(tableName, columnName, "_id = ?", new String[]{String.valueOf(id1)}, null, null, null);
        }catch (Exception e){}
        return data;
    }




    public Cursor validateForLogin(String phone, String password){
        SQLiteDatabase myDb =this.getWritableDatabase();
        return myDb.rawQuery("SELECT _id FROM user WHERE phone = '"+phone+"' AND password = '"+password +"'",null);
    }



    public int deleteDataFrom(String tableName,String wereCluse,long id){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;
        // TODO: return the number of row deleted or 0 if not effected any row
        try {
            result = db.delete(tableName, wereCluse, new String[]{Long.toString(id)});
        }catch (Exception e){
            Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            return result;
        }
    }




    public int deleteTable(String tablename){
        int result = 0 ;
        SQLiteDatabase db = this.getWritableDatabase();
        try{
        result = db.delete(tablename,null,null);
        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(), Toast.LENGTH_SHORT).show();
        }finally {
            return result;
        }
    }



    public int updateTable(String tableName,ContentValues updateValue,String id){
        SQLiteDatabase db = getWritableDatabase();
        //TODO: return the number of row effected or 0 if not update
        return db.update(tableName, updateValue, "phone=?", new String[]{id});
    }



    public String getEmailLast() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String email = "";
        try {
            cursor = db.rawQuery("SELECT email FROM User", null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                email = cursor.getString(cursor.getColumnIndex("email"));
            }
            return email;
        }finally {
            cursor.close();
        }
    }



    public Cursor getLastUserInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM User", null);
    }



    public String getPhoneLastUserDBlocal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String phone = "";
        try {
            cursor = db.rawQuery("SELECT phone FROM User",null);
            if(cursor.getCount() > 0) {
                cursor.moveToLast();
                phone = cursor.getString(cursor.getColumnIndex("phone"));
            }
            return phone;
        }finally {
            cursor.close();
        }
    }



    public Cursor getLast() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String phone = "";
        try {
            cursor = db.rawQuery("SELECT name,lname,image FROM User",null);
            if(cursor.getCount() > 0) {
                cursor.moveToLast();
            }
        }catch (Exception ex){
            Log.e("Error",ex.getMessage());
        }
        return cursor;
    }


    public int getLastIDUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int phone =0;
        try {
            cursor = db.rawQuery("SELECT _id FROM User",null);
            if(cursor.getCount() > 0) {
                cursor.moveToLast();
                phone = cursor.getInt(cursor.getColumnIndex("_id"));
            }
            return phone;
        }finally {
            cursor.close();
        }
    }



    
    public void DropTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '"+tableName+"'");
        Log.e("Droptable SQLite","table succssfull drop it = "+tableName);
    }
}
