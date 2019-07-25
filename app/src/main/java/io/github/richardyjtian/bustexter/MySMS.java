package io.github.richardyjtian.bustexter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class MySMS {

    private static final int SEND_SMS_CODE = 23;

    //Checks if SMS permission is granted
    public static boolean SMSSendPermissionGranted(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    //Requests SMS permission
    public static void requestSMSSendPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Toast.makeText(activity, "SMS access is needed to send texts to Translink", Toast.LENGTH_SHORT).show();
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.SEND_SMS }, SEND_SMS_CODE);
    }

    public static void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public static void sendMultipleSMS(ArrayList<String> busStopsToText, Activity activity) {
        if(busStopsToText.isEmpty()) {
            Toast.makeText(activity, "No stops were texted", Toast.LENGTH_LONG).show();
        }
        else {
            StringBuilder listString = new StringBuilder("Bus stop(s) texted: ");

            String firstBusStop = busStopsToText.get(0);
            listString.append(firstBusStop);
            sendSMS("33333", firstBusStop);

            for(int i = 1; i < busStopsToText.size(); i++) {
                String busStop = busStopsToText.get(i);
                sendSMS("33333", busStop);
                listString.append(", ");
                listString.append(busStop);
            }
            Toast.makeText(activity, listString, Toast.LENGTH_LONG).show();
        }
    }
}
