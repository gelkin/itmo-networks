package ru.ifmo.ctddev.mazin.net.lab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {
    private static final int BUF_SIZE = 1024;

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[BUF_SIZE], BUF_SIZE);
                try {
                    socket.receive(packet);
                    try {
                        Message msg = new Message(packet.getData());
                        Info.INSTANCE.updateInfo(msg);
                    } catch (BadMessageException e) {
                        String errorMsg = "Bad datagram packet from" +
                                          packet.getAddress().getHostAddress() +
                                          ": " + e.getMessage();
                        System.err.println(errorMsg);
                    }
                } catch (IOException ignored) {}
            }
        } catch (SocketException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
