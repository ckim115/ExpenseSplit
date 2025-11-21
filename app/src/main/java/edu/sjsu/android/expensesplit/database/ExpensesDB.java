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
    protected static final String AMOUNT = "amount";
    protected static final String DUE_DATE = "due_date";
    protected static final String COMPLETED = "complete";
    private static final int VERSION = 2;

    public ExpensesDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s DOUBLE, " +
                        "%s TEXT, " +
                        "%s INTEGER NOT NULL DEFAULT 0" +
                        ");",
                DATABASE_NAME, ID, TITLE, NAME, AMOUNT, DUE_DATE, COMPLETED
        );
        db.execSQL(createTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Simple forward migration to add due_date
        if (oldV < 2) {
            db.execSQL("ALTER TABLE " + DATABASE_NAME + " ADD COLUMN " + DUE_DATE + " INTEGER");
        }
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(DATABASE_NAME, null, values);
    }

    public Cursor getExpenses(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(DATABASE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DATABASE_NAME, null, null);
    }

    public int deleteByID(String[] selectionArgs, String selection) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DATABASE_NAME, selection, selectionArgs);
    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(DATABASE_NAME, values, selection, selectionArgs);
    }
}

