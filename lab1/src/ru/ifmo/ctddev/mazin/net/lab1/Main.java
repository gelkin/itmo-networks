package ru.ifmo.ctddev.mazin.net.lab1;


public class Main {
    public static final int SLEEP_TIME = 5000;

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Port must be specified as first argument");
        }
        int port = Integer.parseInt(args[0]);
        new Thread(new Client(port)).start();
        new Thread(new Server(port)).start();
        new Thread(new Output()).start();
    }
}
