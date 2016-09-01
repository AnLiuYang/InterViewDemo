package com.yang.interviewdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/9/1.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    private String TAG = "yang";

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createStoreTable(db);
        System.out.println("创建数据库");
    }

    /**
     * 创建数据库表
     */
    private void createStoreTable(SQLiteDatabase db) {
        StringBuffer sql = new StringBuffer("CREATE TABLE "
                + StoreInfo.TABLE_NAME + "(");
        final int count = StoreInfo.KEYS.length;
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                sql.append(",");
            }
            sql.append(StoreInfo.KEYS[i]);
            sql.append(" ");
            sql.append(StoreInfo.TYPE[i]);
        }
        sql.append(");");
        Log.e(TAG, sql.toString());
        db.execSQL(sql.toString());
    }

    /*
     * 版本更新器
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        System.out.println("更新数据库");
    }

    /*
     * 添加记录
     */
    int add(String table, String[] keys, Object[] values) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        StringBuffer sql = new StringBuffer("insert into ");
        sql.append(table);
        sql.append("(");

        boolean is_first = true;
        int count = 0;
        for (String col : keys) {
            if (!is_first) {
                sql.append(",");
            }
            sql.append(col);
            is_first = false;
            count++;
        }
        sql.append(") values(");
        is_first = true;
        for (int i = 0; i < count; i++) {
            if (!is_first) {
                sql.append(",");
            }
            sql.append("?");
            is_first = false;
        }
        sql.append(");");
        Log.v(TAG, sql.toString());
        try {
            Log.i(TAG ,"sql.toString() : " + sql.toString());
            myDB.execSQL(sql.toString(), values);
            Log.v(TAG, "add success!!!!");
            Cursor cursor = myDB.rawQuery("select last_insert_rowid() from "
                    + table, null);
            int strid = 0;
            if (cursor.moveToFirst())
                strid = cursor.getInt(0);
            return strid;
        } catch (Exception e) {
            Log.e(TAG, "add error", e);
            return 0;
        } finally {
            myDB.close();
        }
    }

    /*
     * 获取所有记录
     */
    Cursor getAll(String table, String[] columns) {
        // 需要返回的列
        SQLiteDatabase myDB = this.getReadableDatabase();
        // 调用SQLiteDatabase类的query函数查询记录
        Cursor res = myDB.query(table, columns, null, null, null, null, null);
        return res;
    }

    /*
     * 修改记录
     */
    long alterByID(String table, int id, String keys, Object values) {

        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues c_values = new ContentValues();
        if (values instanceof Integer)
            c_values.put(keys, (Integer) values);
        else
            c_values.put(keys, (String) values);
        String whereClause = "id=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        long res = myDB.update(table, c_values, whereClause, whereArgs);
        myDB.close();
        return res;
    }

    /*
     * 删除记录
     */
    void deleteById(String table, int id) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        String whereClause = "id=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        myDB.delete(table, whereClause, whereArgs);
        myDB.close();
    }

    /*
     * 删除记录
     */
    void deleteAll(String table) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.delete(table, null, null);
        myDB.close();
    }

    /*
     * 获得单条记录
     */
    Cursor get(String tableName, String name, String[] columns) {
        // TODO Auto-generated method stub
        SQLiteDatabase myDB = this.getReadableDatabase();
        String selection = "name=?";
        String[] selectionArgs = new String[] {name};
        Cursor res = myDB.query(tableName, columns, selection, selectionArgs,
                null, null, null);
        return res;
    }

    Cursor get(String tableName, int id, String[] columns) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        String selection = "id=?";
        String[] selectionArgs = new String[] { id + "" };
        Cursor res = myDB.query(tableName, columns, selection, selectionArgs,
                null, null, null);
        return res;
    }

    /*
     * 获取某条记录是否存在
     */
    boolean checkIfExist(String tableName, String[] key, String[] value) {
        SQLiteDatabase myDB = this.getWritableDatabase();

        StringBuffer selection = new StringBuffer();
        for (String k : key) {
            selection.append(k);
            selection.append("=?");
        }
        Cursor res = myDB.query(tableName, new String[] { "id" },
                selection.toString(), value, null, null, null);
        try{
            if (res.moveToFirst()) {
                myDB.close();
                return true;
            } else {
                myDB.close();
                return false;
            }
        }finally{
            if(res != null)
                res.close();
        }
    }
}
