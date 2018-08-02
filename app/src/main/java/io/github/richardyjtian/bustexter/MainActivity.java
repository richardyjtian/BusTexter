package io.github.richardyjtian.bustexter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int SEND_SMS_CODE = 23; //added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo: verify permissions in if statement
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(!SMSSendPermissionGranted())
            requestSMSSendPermission();
        else{
            if (hour < 15)
                sendSMS("33333", "56767 410");
            else
                sendSMS("33333", "58624 410");
        }
    }

    private boolean SMSSendPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

    }

    //Requesting permission
    private void requestSMSSendPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, SEND_SMS_CODE);
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
