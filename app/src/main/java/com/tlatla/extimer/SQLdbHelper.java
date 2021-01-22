package com.tlatla.extimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SQLdbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "timer.db";
    public static final String TABLE_NAME = "timer_table";
    public static final String COLUMN_DATA = "timer_data";
    public static final String COLUMN_ID = "id";
    public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_DATA + " TEXT)";
    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public static SQLdbHelper inst;

    private SQLiteDatabase db;
    private Context context;

    public SQLdbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        onCreate(db);
    }

    public static SQLdbHelper getInst(Context context) {
        if (inst == null)
            inst = new SQLdbHelper(context);
        return inst;
    }

    public Cursor getAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    //추가
    public long insertData(String data) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, data);
        Toast.makeText(context, "저장되었습니다!", Toast.LENGTH_SHORT).show();
        return db.insert(TABLE_NAME, null, values);
    }
    //삭제
    public void deleteData(String data) {
        System.out.println("삭제하기 " + data);
        int id = getID_fromData(data);
        db.delete(TABLE_NAME, COLUMN_DATA + "=? and "
                + COLUMN_ID + "=?", new String[]{data, String.valueOf(id)});
        Toast.makeText(context, "삭제되었습니다!", Toast.LENGTH_SHORT).show();
    }
    //수정
    public void updateData(String new_data, String old_data) {
        System.out.println("수정하기 " + old_data + " -> " + new_data);
        long id = getID_fromData(old_data);
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, new_data);
        db.update(TABLE_NAME, values,COLUMN_DATA + " = ? AND "  + COLUMN_ID + " = ? ", new String[]{old_data, String.valueOf(id)}
        );
        Toast.makeText(context, "수정되었습니다!", Toast.LENGTH_SHORT).show();
    }

    public int getID_fromData(String data) {
        System.out.println("삭제 또는 수정할 내용은: " + data);
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID}, COLUMN_DATA + "=?", new String[]{String.valueOf(data)}, null, null, null);
        int id = 0;
        while (cursor.moveToNext()) {
            int column = cursor.getColumnIndex(COLUMN_ID);
            id = cursor.getInt(column);
            System.out.println(data + " 의 id는 " + id);
        }
        cursor.close();
        return id;
    }

    public ArrayList<ArrayList<String>> loadDataList(){
        ArrayList<ArrayList<String>> datas = new ArrayList<>();
        int cnt=0;
        try {
            Cursor c = getAllData();
            if(c.moveToFirst()){
                do{
                    ArrayList<String> data = new ArrayList<>();
                    String [] splits = (c.getString(1)).split(",");
                    for(int i=0; i<splits.length;i++){
                        data.add(splits[i]);
                    }
                    Log.d("** db 내용",c. getInt(0) +" "+ data);
                    datas.add(data);
                    cnt++;
                }while (c.moveToNext());
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datas;
    }
}