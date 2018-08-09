package io.github.richardyjtian.bustexter;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class DBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1; //change version if add other descriptors to entry class
    private static final String DATABASE_NAME = "entries.db";
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_STOPNUMBER = "stopNumber";
    private static final String COLUMN_BUSNUMBER = "busNumber";
    private static final String COLUMN_BEGINPERIOD = "beginPeriod";
    private static final String COLUMN_ENDPERIOD = "endPeriod";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_ENTRIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + ", " +
                COLUMN_STOPNUMBER + " TEXT" + ", " +
                COLUMN_BUSNUMBER + " TEXT" + ", " +
                COLUMN_BEGINPERIOD + " TEXT" + ", " +
                COLUMN_ENDPERIOD + " TEXT" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    //Add a new row to the database
    public void addEntry(Entry entry){
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOPNUMBER, entry.getStopNumber());
        values.put(COLUMN_BUSNUMBER, entry.getBusNumber());
        values.put(COLUMN_BEGINPERIOD, entry.getBeginPeriod());
        values.put(COLUMN_ENDPERIOD, entry.getEndPeriod());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ENTRIES, null, values);
        db.close();
    }

    //Delete an entry from the database
    public void deleteEntry(String stopNumber){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_STOPNUMBER + "=\"" + stopNumber + "\";");
    }

    //Print out the database as a string
    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("stopNumber")) != null) {
                dbString += "Texting: " + c.getString(c.getColumnIndex("stopNumber")) + " " + c.getString(c.getColumnIndex("busNumber")) +
                        " between " + c.getString(c.getColumnIndex("beginPeriod")) + " o'clock & " + c.getString(c.getColumnIndex("endPeriod")) + " o'clock";
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    //Returns the bus stop to text at a given time
    public String busStopToText(int time){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("stopNumber")) != null) {
                int beginPeriod = c.getInt(c.getColumnIndex("beginPeriod"));
                int endPeriod = c.getInt(c.getColumnIndex("endPeriod"));
                if(time > beginPeriod && time < endPeriod){
                    return c.getString(c.getColumnIndex("stopNumber")) + " " + c.getString(c.getColumnIndex("busNumber"));
                }
            }
            c.moveToNext();
        }
        db.close();
        return null;
    }

}
