package io.github.richardyjtian.bustexter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText stopNumberInput;
    EditText busNumberInput;
    EditText beginPeriodInput;
    EditText endPeriodInput;
    EditText busNameInput;
    public static DBHandler dbHandler;

    // instance of custom array adaptor
    public EntryArrayAdapter myEntryArrayAdapter;

    // dynamic array of entries (populate at run time)
    public ArrayList<Entry> myEntryArray = new ArrayList<>();

    //todo: organize array by start time (if same start time, earlier end time comes first)
    //todo: location based text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopNumberInput = (EditText) findViewById(R.id.stopNumberInput);
        busNumberInput = (EditText) findViewById(R.id.busNumberInput);
        beginPeriodInput = (EditText) findViewById(R.id.beginPeriodInput);
        endPeriodInput = (EditText) findViewById(R.id.endPeriodInput);
        busNameInput = (EditText) findViewById(R.id.busNameInput);

        busNameInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Done button pressed on keyboard
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addButtonClicked(findViewById(android.R.id.content));
                    return true;
                }
                return false;
            }
        });

        dbHandler = new DBHandler(this, null, null, 1);
        myEntryArray = dbHandler.databaseToArrayList();
        updateArrayAdapter();

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(!SMS.SMSSendPermissionGranted(this))
            SMS.requestSMSSendPermission(this);
        else{
            ArrayList<String> busStopsToText = dbHandler.busStopToText(hour);
            for(int i = 0; i < busStopsToText.size(); i++) {
                SMS.sendSMS("33333", busStopsToText.get(i));
            }
        }
    }

    //Add an entry to the database
    public void addButtonClicked(View view){
        String stopNumber = stopNumberInput.getText().toString();
        String busNumber = busNumberInput.getText().toString();
        String beginPeriod = beginPeriodInput.getText().toString();
        String endPeriod = endPeriodInput.getText().toString();
        String busName = busNameInput.getText().toString();
        if(!stopNumber.equals("") && !busNumber.equals("") && !beginPeriod.equals("") && !endPeriod.equals("")
                && Integer.parseInt(beginPeriod) >= 0 && Integer.parseInt(endPeriod) <= 24) {

            Entry entry = new Entry(stopNumber, busNumber, beginPeriod, endPeriod, busName);
            myEntryArray = dbHandler.addEntry(entry);
            updateArrayAdapter();
            clearUserInputs();
        }
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

    public void clearUserInputs(){
        stopNumberInput.setText("");
        busNumberInput.setText("");
        beginPeriodInput.setText("");
        endPeriodInput.setText("");
        busNameInput.setText("");
    }
}
