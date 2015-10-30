package ru.ifmo.ctddev.mazin.net.lab1;

public class NodeInfo {
    private int missedNumber;

    public String hostname;
    public long lastSent;
    public long lastReceived;

    public NodeInfo(String hostname, long lastSent, long lastReceived) {
        this.hostname = hostname;
        this.lastSent = lastSent;
        this.lastReceived = lastReceived;
        missedNumber = 0;
    }

    public int getMissedNumber() {
        return missedNumber;
    }

    public void incMissedNumber() {
        ++missedNumber;
    }

}
