package io.github.richardyjtian.bustexter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 3; //change version if add other descriptors to entry class
    private static final String DATABASE_NAME = "entries.db";
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_STOPNUMBER = "stopNumber";
    private static final String COLUMN_BUSNUMBER = "busNumber";
    private static final String COLUMN_BEGINPERIOD = "beginPeriod";
    private static final String COLUMN_ENDPERIOD = "endPeriod";
    private static final String COLUMN_BUSNAME = "busName";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_ENTRIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + ", " +
                COLUMN_STOPNUMBER + " INTEGER" + ", " +
                COLUMN_BUSNUMBER + " INTEGER" + ", " +
                COLUMN_BEGINPERIOD + " INTEGER" + ", " +
                COLUMN_ENDPERIOD + " INTEGER" + ", " +
                COLUMN_BUSNAME + " TEXT" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    //Add a new row to the database
    public ArrayList<Entry> addEntry(Entry entry){
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOPNUMBER, entry.getStopNumber());
        values.put(COLUMN_BUSNUMBER, entry.getBusNumber());
        values.put(COLUMN_BEGINPERIOD, entry.getBeginPeriod());
        values.put(COLUMN_ENDPERIOD, entry.getEndPeriod());
        values.put(COLUMN_BUSNAME, entry.getBusName());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ENTRIES, null, values);
        db.close();
        return databaseToArrayList();
    }

    //Delete an entry from the database
    public void deleteEntry(int stopNumber){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_STOPNUMBER + "=\"" + stopNumber + "\";");
    }

    //Print out the database as a string
//    public String databaseToString() {
//        String dbString = "";
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_ENTRIES + " WHERE 1";
//
//        //Cursor point to a location in your results
//        Cursor c = db.rawQuery(query, null);
//        //Move to the first row in your results
//        c.moveToFirst();
//
//        while (!c.isAfterLast()) {
//            if (c.getString(c.getColumnIndex("stopNumber")) != null) {
//                dbString += "Texting: " + c.getString(c.getColumnIndex("stopNumber")) + " " + c.getString(c.getColumnIndex("busNumber")) +
//                        " between " + c.getString(c.getColumnIndex("beginPeriod")) + " o'clock & " + c.getString(c.getColumnIndex("endPeriod")) + " o'clock";
//                dbString += "\n";
//            }
//            c.moveToNext();
//        }
//        db.close();
//        return dbString;
//    }

    //Returns a copy of the database as an arraylist
    public ArrayList<Entry> databaseToArrayList() {
        ArrayList<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES + " WHERE 1" + " ORDER BY " + COLUMN_BEGINPERIOD + " ASC, " + COLUMN_ENDPERIOD + " ASC, "
                + COLUMN_BUSNAME + " ASC";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("stopNumber")) != null) {
                Entry entry = new Entry(c.getInt(c.getColumnIndex("stopNumber")), c.getInt(c.getColumnIndex("busNumber")),
                        c.getInt(c.getColumnIndex("beginPeriod")), c.getInt(c.getColumnIndex("endPeriod")), c.getString(c.getColumnIndex("busName")));
                entries.add(entry);
            }
            c.moveToNext();
        }
        db.close();
        return entries;
    }


    //Returns bus stops to text at a given time
    public ArrayList<String> busStopToText(int time){
        ArrayList<String> bus_stops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES + " WHERE 1" + " ORDER BY " + COLUMN_BEGINPERIOD + " ASC, " + COLUMN_ENDPERIOD + " ASC, "
                + COLUMN_BUSNAME + " ASC";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            String stopNumber = c.getString(c.getColumnIndex("stopNumber"));
            if (stopNumber != null) {
                int beginPeriod = c.getInt(c.getColumnIndex("beginPeriod"));
                int endPeriod = c.getInt(c.getColumnIndex("endPeriod"));
                if((time >= beginPeriod && time < endPeriod) || (endPeriod < beginPeriod && time >= beginPeriod) || (endPeriod < beginPeriod && time < endPeriod)){
                    String busNumber = c.getString(c.getColumnIndex("busNumber"));
                    bus_stops.add(stopNumber + " " + busNumber);
                }
            }
            c.moveToNext();
        }
        db.close();
        return bus_stops;
    }

}
