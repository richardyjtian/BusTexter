package io.github.richardyjtian.bustexter;

public class Entry {

    private int _id;
    private String stopNumber;
    private String busNumber;
    private String beginPeriod;
    private String endPeriod;

    public Entry(String stopNumber, String busNumber, String beginPeriod, String endPeriod) {
        this.stopNumber = stopNumber;
        this.busNumber = busNumber;
        this.beginPeriod = beginPeriod;
        this.endPeriod = endPeriod;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(String stopNumber) {
        this.stopNumber = stopNumber;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBeginPeriod() {
        return beginPeriod;
    }

    public void setBeginPeriod(String beginPeriod) {
        this.beginPeriod = beginPeriod;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(String endPeriod) {
        this.endPeriod = endPeriod;
    }
}
