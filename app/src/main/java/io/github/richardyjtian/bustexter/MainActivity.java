package io.github.richardyjtian.bustexter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText stopNumberInput;
    EditText busNumberInput;
    EditText beginPeriodInput;
    EditText endPeriodInput;
    TextView userText;
    DBHandler dbHandler;

    //todo: location based text
    //todo: make it easier to delete an entry
    //todo: allow bus texts inside the app on click of button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopNumberInput = (EditText) findViewById(R.id.stopNumberInput);
        busNumberInput = (EditText) findViewById(R.id.busNumberInput);
        beginPeriodInput = (EditText) findViewById(R.id.beginPeriodInput);
        endPeriodInput = (EditText) findViewById(R.id.endPeriodInput);
        userText = (TextView) findViewById(R.id.userText);
        dbHandler = new DBHandler(this, null, null, 1);
        printDatabase();


        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(!SMS.SMSSendPermissionGranted(this))
            SMS.requestSMSSendPermission(this);
        else{
            String busStopToText = dbHandler.busStopToText(hour);
            if(busStopToText != null)
                SMS.sendSMS("33333", busStopToText);
        }
    }

    //Add an entry to the database
    public void addButtonClicked(View view){
        String stopNumber = stopNumberInput.getText().toString();
        String busNumber = busNumberInput.getText().toString();
        String beginPeriod = beginPeriodInput.getText().toString();
        String endPeriod = endPeriodInput.getText().toString();
        if(!stopNumber.equals("") && !busNumber.equals("") && !beginPeriod.equals("") && !endPeriod.equals("")
                && Integer.parseInt(beginPeriod) >= 0 && Integer.parseInt(endPeriod) <= 24 && Integer.parseInt(endPeriod) > Integer.parseInt(beginPeriod)) {

            Entry entry = new Entry(stopNumberInput.getText().toString(), busNumberInput.getText().toString(),
                    beginPeriodInput.getText().toString(), endPeriodInput.getText().toString());
            dbHandler.addEntry(entry);
        }
        printDatabase();
    }

    //Delete an entry from the database
    public void deleteButtonClicked(View view){
        String inputText = stopNumberInput.getText().toString();
        dbHandler.deleteEntry(inputText);
        printDatabase();
    }

    public void sendSMSClicked(View view){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(!SMS.SMSSendPermissionGranted(this))
            SMS.requestSMSSendPermission(this);
        else{
            String busStopToText = dbHandler.busStopToText(hour);
            if(busStopToText != null)
                SMS.sendSMS("33333", busStopToText);
        }
    }

    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        userText.setText(dbString);
        stopNumberInput.setText("");
        busNumberInput.setText("");
        beginPeriodInput.setText("");
        endPeriodInput.setText("");

    }
}
