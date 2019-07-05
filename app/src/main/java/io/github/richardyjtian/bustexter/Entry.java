package io.github.richardyjtian.bustexter;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class Entry {

    private int _id;
    private int stopNumber;
    private int busNumber;
    private int beginPeriod;
    private int endPeriod;
    private String busName = "";
    private String address = "";
    private double latitude = 0;
    private double longitude = 0;

    // Use this to initialize an entry
    public Entry(int stopNumber, int busNumber, int beginPeriod, int endPeriod, String busName, String address, Activity activity) {
        this.stopNumber = stopNumber;
        this.busNumber = busNumber;
        this.beginPeriod = beginPeriod;
        this.endPeriod = endPeriod;
        this.busName = busName;

        setLocation(address, activity);
    }

    private void setLocation(String address, Activity activity) {
        if(address.isEmpty()) {
            Toast.makeText(activity, "No address entered", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder coder = new Geocoder(activity);
        List<Address> addresses;
        try {
            // Get top 5 results
            addresses = coder.getFromLocationName(address,5);
            if(addresses.isEmpty()) {
                Toast.makeText(activity, "Address does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            Address location = addresses.get(0);
            String addressLine = location.getAddressLine(0);
            if(addressLine == null) {
                Toast.makeText(activity, "Address does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!addressLine.contains(", BC ")) {
                Toast.makeText(activity, "Address is outside of BC", Toast.LENGTH_SHORT).show();
                return;
            }

            this.address = addressLine.split(", BC ")[0];
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();

        } catch (IOException e) {
            Toast.makeText(activity, "Address does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    // Used by the DBHandler to set the fields
    public Entry(int stopNumber, int busNumber, int beginPeriod, int endPeriod, String busName, String address, Double latitude, Double longitude) {
        this.stopNumber = stopNumber;
        this.busNumber = busNumber;
        this.beginPeriod = beginPeriod;
        this.endPeriod = endPeriod;
        this.busName = busName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }

    public int getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(int busNumber) {
        this.busNumber = busNumber;
    }

    public int getBeginPeriod() {
        return beginPeriod;
    }

    public void setBeginPeriod(int beginPeriod) {
        this.beginPeriod = beginPeriod;
    }

    public int getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(int endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}