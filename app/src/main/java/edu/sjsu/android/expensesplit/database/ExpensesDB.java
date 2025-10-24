package edu.sjsu.android.expensesplit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ExpensesDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ExpensesDB";
    protected static final String ID = "_id";
    protected static final String TITLE = "title";
    protected static final String NAME = "name";
    protected static final String TYPE = "type";
    protected static final String AMOUNT = "amount";
    private static final int VERSION = 1;

    public ExpensesDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = String.format("CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s DOUBLE);", DATABASE_NAME, ID, TITLE, NAME, TYPE, AMOUNT);
        db.execSQL(createTable);
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(DATABASE_NAME, null, values);
    }

    public Cursor getAllExpenses() {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(DATABASE_NAME, null, null, null, null, null, null);
    }

    public Cursor getByID(String[] selectionArgs, String selection) {
        SQLiteDatabase db = getWritableDatabase();
//        String selection = "_id = ?";
//        String[] selectionArgs = { String.valueOf(rowId) };

        return db.query(DATABASE_NAME, null, selection, selectionArgs, null, null, null);
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DATABASE_NAME, null, null);
    }


    public int deleteByID(String[] selectionArgs, String selection) {
        SQLiteDatabase db = getWritableDatabase();
//        String selection = "_id = ?";
//        String[] selectionArgs = { String.valueOf(rowId) };

        return db.delete(DATABASE_NAME, selection, selectionArgs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
