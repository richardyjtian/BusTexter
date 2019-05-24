package io.github.richardyjtian.bustexter;

public class Entry {

    private int _id;
    private int stopNumber;
    private int busNumber;
    private int beginPeriod;
    private int endPeriod;
    private String busName;

    public Entry(int stopNumber, int busNumber, int beginPeriod, int endPeriod, String busName) {
        this.stopNumber = stopNumber;
        this.busNumber = busNumber;
        this.beginPeriod = beginPeriod;
        this.endPeriod = endPeriod;
        this.busName = busName;
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
}
