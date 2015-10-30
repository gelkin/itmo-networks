package ru.ifmo.ctddev.mazin.net.lab1;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

public class Client implements Runnable {


    private final int port;

    public Client(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String hostname = InetAddress.getLocalHost().getHostName();
            while (true) {
                try {
                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    searchInterfaces:
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface network = interfaces.nextElement();
                        byte[] mac = network.getHardwareAddress();
                        if (mac != null) {
                            List<InterfaceAddress> addresses = network.getInterfaceAddresses();
                            for (InterfaceAddress address : addresses) {
                                if (address.getBroadcast() != null) {
                                    Message msg = new Message(mac, hostname);
                                    byte[] data = msg.getData();
                                    DatagramPacket packet = new DatagramPacket(data, data.length, address.getBroadcast(), port);
                                    socket.send(packet);
                                    break searchInterfaces;
                                }
                            }
                        }
                    }
                    Thread.sleep(Main.SLEEP_TIME);
                } catch (InterruptedException | IOException e) {
                    System.err.println(e);
                    System.exit(1);
                }
            }
        } catch (SocketException | UnknownHostException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
