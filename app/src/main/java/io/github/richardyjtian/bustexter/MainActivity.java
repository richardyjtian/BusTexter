package io.github.richardyjtian.bustexter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Switch locationOptionSwitch;

    // Instance of database handler
    public static DBHandler dbHandler;

    // Instance of custom array adaptor
    public EntryArrayAdapter myEntryArrayAdapter;

    // Dynamic array of entries (populate at run time)
    public ArrayList<Entry> myEntryArray = new ArrayList<>();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(this, null, null, 1);
        myEntryArray = dbHandler.databaseToArrayList();
        updateArrayAdapter();

        locationOptionSwitch = findViewById(R.id.locationOptionSwitch);
        SharedPreferences sharedPrefs = getSharedPreferences("io.github.richardyjtian.bustexter", MODE_PRIVATE);
        locationOptionSwitch.setChecked(sharedPrefs.getBoolean("locationOption", false));

        // Need SMS permission
        if (!MySMS.SMSSendPermissionGranted(this)) {
            MySMS.requestSMSSendPermission(this);
            return;
        }
        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // Don't care about location
        if (!locationOptionSwitch.isChecked()) {
            MySMS.sendMultipleSMS(dbHandler.busStopsToText(hour), this);
            return;
        }

        // Geocoder isn't present so location won't work
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "Geocoder isn't present, so location won't work", Toast.LENGTH_SHORT).show();
            MySMS.sendMultipleSMS(dbHandler.busStopsToText(hour), this);
            return;
        }

        // Do care about location
        // No Location permission so just send according to time
        if (!MyLocation.LocationPermissionGranted(this)) {
            MyLocation.requestLocationPermission(this);
            MySMS.sendMultipleSMS(dbHandler.busStopsToText(hour), this);
            return;
        }

        // Location permission granted so send according to time and location
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                    MySMS.sendMultipleSMS(dbHandler.busStopsToText(hour, location), MainActivity.this);
                else
                    MySMS.sendMultipleSMS(dbHandler.busStopsToText(hour), MainActivity.this);
            }
        });
        client.getLastLocation().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MySMS.sendMultipleSMS(dbHandler.busStopsToText(hour), MainActivity.this);
            }
        });
    }
    /**********************************************************************************************/
    // Save the status of the switch
    public void switchToggled(View v) {
        if (locationOptionSwitch.isChecked()) {
            if (!MyLocation.LocationPermissionGranted(this)) {
                MyLocation.requestLocationPermission(this);
            }
            if (MyLocation.LocationPermissionGranted(this)) {
                SharedPreferences.Editor editor = getSharedPreferences("io.github.richardyjtian.bustexter", MODE_PRIVATE).edit();
                editor.putBoolean("locationOption", true);
                editor.apply();
                return;
            }
            else {
                locationOptionSwitch.setChecked(false);
            }
        }
        SharedPreferences.Editor editor = getSharedPreferences("io.github.richardyjtian.bustexter", MODE_PRIVATE).edit();
        editor.putBoolean("locationOption", false);
        editor.apply();
    }

    /**********************************************************************************************/
    // Add an entry to the database
    public void addButtonClicked(View view){
        addEntry(view);
    }

    AlertDialog alertDialog;
    public void addEntry(View view){
        // create a new AlertDialog Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the message and the Title
        builder.setTitle("Add Bus Stop");

        // setup the dialog so that it cannot be cancelled by the back key (optional)
        builder.setCancelable(true);

        // We need a layout inflater to read our XML file and construct the layout
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the dialogbox.xml layout file
        View theDialog = inflater.inflate(R.layout.dialogbox, null);

        // Focus keyboard on restaurant name
        EditText busName = theDialog.findViewById(R.id.busNameInput);
        busName.requestFocus();
        showKeyboard();

        // On finish of typing note
        EditText address = theDialog.findViewById(R.id.addressInput);
        address.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Done button pressed on keyboard
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    createNewEntry();
                    closeKeyboard();
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        // Set as dialog view
        builder.setView(theDialog);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewEntry();
                closeKeyboard();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void createNewEntry() {
        EditText stopNumberET = alertDialog.findViewById(R.id.stopNumberInput);
        EditText busNumberET = alertDialog.findViewById(R.id.busNumberInput);
        EditText beginPeriodET = alertDialog.findViewById(R.id.beginPeriodInput);
        EditText endPeriodET = alertDialog.findViewById(R.id.endPeriodInput);

        String stopNumber = stopNumberET.getText().toString();
        String busNumber = busNumberET.getText().toString();
        String beginPeriod = beginPeriodET.getText().toString();
        String endPeriod = endPeriodET.getText().toString();

        if(!stopNumber.isEmpty() && !busNumber.isEmpty() && !beginPeriod.isEmpty() && !endPeriod.isEmpty()) {
            EditText busNameET = alertDialog.findViewById(R.id.busNameInput);
            EditText addressET = alertDialog.findViewById(R.id.addressInput);
            String busName = busNameET.getText().toString();
            String address = addressET.getText().toString();

            int beginPeriod_int = Integer.parseInt(beginPeriod);
            int endPeriod_int = Integer.parseInt(endPeriod);

            if(beginPeriod_int >= 0 && beginPeriod_int <= 24 && endPeriod_int >= 0 && endPeriod_int <= 24) {
                Entry entry = new Entry(Integer.parseInt(stopNumber), Integer.parseInt(busNumber), beginPeriod_int, endPeriod_int, busName, address, this);
                myEntryArray = dbHandler.addEntry(entry);
                updateArrayAdapter();
            }
        }
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void updateArrayAdapter(){
        myEntryArrayAdapter = new EntryArrayAdapter(this, android.R.layout.simple_list_item_1, myEntryArray);

        // get handles to the list view in the main activity layout
        ListView listView = (ListView) findViewById(R.id.listView);

        // set the adaptor view
        listView.setAdapter(myEntryArrayAdapter);

        // Update the list with previously saved data
        myEntryArrayAdapter.notifyDataSetChanged();
    }
}
