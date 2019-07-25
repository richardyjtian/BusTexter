package io.github.richardyjtian.bustexter;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.location.Location;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 5; //change version if add other descriptors to entry class
    private static final String DATABASE_NAME = "entries.db";
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_STOPNUMBER = "stopNumber";
    private static final String COLUMN_BUSNUMBER = "busNumber";
    private static final String COLUMN_BEGINPERIOD = "beginPeriod";
    private static final String COLUMN_ENDPERIOD = "endPeriod";
    private static final String COLUMN_BUSNAME = "busName";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

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
                COLUMN_BUSNAME + " TEXT" + ", " +
                COLUMN_ADDRESS + " TEXT" + ", " +
                COLUMN_LATITUDE + " REAL" + ", " +
                COLUMN_LONGITUDE + " REAL" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    // Add a new row to the database
    public ArrayList<Entry> addEntry(Entry entry){
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOPNUMBER, entry.getStopNumber());
        values.put(COLUMN_BUSNUMBER, entry.getBusNumber());
        values.put(COLUMN_BEGINPERIOD, entry.getBeginPeriod());
        values.put(COLUMN_ENDPERIOD, entry.getEndPeriod());
        values.put(COLUMN_BUSNAME, entry.getBusName());
        values.put(COLUMN_ADDRESS, entry.getAddress());
        values.put(COLUMN_LATITUDE, entry.getLatitude());
        values.put(COLUMN_LONGITUDE, entry.getLongitude());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ENTRIES, null, values);
        db.close();
        return databaseToArrayList();
    }

    // Deletes an entry from the database
    public void deleteEntry(int stopNumber){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_STOPNUMBER + "=\"" + stopNumber + "\";");
        db.close();
    }

    // Returns a copy of the database as an arraylist
    public ArrayList<Entry> databaseToArrayList() {
        ArrayList<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES + " WHERE 1" + " ORDER BY " + COLUMN_BEGINPERIOD + " ASC, " +
                COLUMN_ENDPERIOD + " ASC, " + COLUMN_ADDRESS + " ASC," + COLUMN_BUSNAME + " ASC";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("stopNumber")) != null) {
                int stopNumber = c.getInt(c.getColumnIndex("stopNumber"));
                int busNumber = c.getInt(c.getColumnIndex("busNumber"));
                int beginPeriod = c.getInt(c.getColumnIndex("beginPeriod"));
                int endPeriod = c.getInt(c.getColumnIndex("endPeriod"));
                String busName = c.getString(c.getColumnIndex("busName"));
                String address = c.getString(c.getColumnIndex("address"));
                double latitude = c.getDouble(c.getColumnIndex("latitude"));
                double longitude = c.getDouble(c.getColumnIndex("longitude"));

                Entry entry = new Entry(stopNumber, busNumber, beginPeriod, endPeriod, busName, address, latitude, longitude);
                entries.add(entry);
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return entries;
    }


    // Returns bus stops to text at a given time and location
    public ArrayList<String> busStopsToText(int time){
        ArrayList<String> bus_stops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_STOPNUMBER + ", " + COLUMN_BUSNUMBER + " FROM " + TABLE_ENTRIES +
                " WHERE " +
                    "(" + time + " >= " + COLUMN_BEGINPERIOD + " AND " + time + " < " + COLUMN_ENDPERIOD + ") OR " +
                    "(" + COLUMN_ENDPERIOD + " < " + COLUMN_BEGINPERIOD + " AND " + time + " >= " + COLUMN_BEGINPERIOD + ") OR " +
                    "(" + COLUMN_ENDPERIOD + " < " + COLUMN_BEGINPERIOD + " AND " + time + " < " + COLUMN_ENDPERIOD + ")" +
                " ORDER BY " + COLUMN_BEGINPERIOD + " ASC, " + COLUMN_ENDPERIOD + " ASC, " + COLUMN_BUSNAME + " ASC";

        // (time >= beginPeriod && time < endPeriod) || (endPeriod < beginPeriod && time >= beginPeriod) || (endPeriod < beginPeriod && time < endPeriod)

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            String stopNumber = c.getString(c.getColumnIndex("stopNumber"));
            String busNumber = c.getString(c.getColumnIndex("busNumber"));
            if (stopNumber != null && busNumber != null) {
                bus_stops.add(stopNumber + " " + busNumber);
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return bus_stops;
    }

    // Returns bus stops to text at a given time and location if the distance is within 1000m
    private final float distanceThreshold = 1500; // distance in meters
    public ArrayList<String> busStopsToText(int time, Location currLocation){
        ArrayList<String> bus_stops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_STOPNUMBER + ", " + COLUMN_BUSNUMBER + ", " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE +
                " FROM " + TABLE_ENTRIES +
                " WHERE " +
                    "(" + time + " >= " + COLUMN_BEGINPERIOD + " AND " + time + " < " + COLUMN_ENDPERIOD + ") OR " +
                    "(" + COLUMN_ENDPERIOD + " < " + COLUMN_BEGINPERIOD + " AND " + time + " >= " + COLUMN_BEGINPERIOD + ") OR " +
                    "(" + COLUMN_ENDPERIOD + " < " + COLUMN_BEGINPERIOD + " AND " + time + " < " + COLUMN_ENDPERIOD + ")" +
                " ORDER BY " + COLUMN_BEGINPERIOD + " ASC, " + COLUMN_ENDPERIOD + " ASC, " + COLUMN_BUSNAME + " ASC";

        // (time >= beginPeriod && time < endPeriod) || (endPeriod < beginPeriod && time >= beginPeriod) || (endPeriod < beginPeriod && time < endPeriod)

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            String stopNumber = c.getString(c.getColumnIndex("stopNumber"));
            String busNumber = c.getString(c.getColumnIndex("busNumber"));
            double latitude = c.getDouble(c.getColumnIndex("latitude"));
            double longitude = c.getDouble(c.getColumnIndex("longitude"));
            if (stopNumber != null && busNumber != null && latitude != 0 && longitude != 0) {
                Location stopLocation = new Location("stopLocation");
                stopLocation.setLatitude(latitude);
                stopLocation.setLongitude(longitude);
                float distanceToStop = stopLocation.distanceTo(currLocation);
                // Check if the current distance to the stop is below the distance threshold
                if(distanceToStop < distanceThreshold)
                    bus_stops.add(stopNumber + " " + busNumber);
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return bus_stops;
    }
}
