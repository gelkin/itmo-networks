package ru.ifmo.ctddev.mazin.net.lab1;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Info {
    public static final Info INSTANCE = new Info();
    public static final int MAX_MISSED_PACKETS = 5;

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private final Map<String, NodeInfo> nodes = new ConcurrentHashMap<>();

    private Info() {}

    public Map<String, NodeInfo> getNodes() {
        return nodes;
    }

    public synchronized void updateInfo(Message msg) {
        String mac = MacToString(msg.getMac());
        if (nodes.containsKey(mac)) {
            NodeInfo nodeInfo = nodes.get(mac);
            if (nodeInfo.getMissedNumber() >= MAX_MISSED_PACKETS) {
                nodes.remove(mac);
            } else {
                nodeInfo.lastSent = msg.getUnixTimestamp();
                nodeInfo.lastReceived = System.currentTimeMillis();
            }
        } else {
            NodeInfo nodeInfo = new NodeInfo(msg.getHostname(),
                                             msg.getUnixTimestamp(),
                                             System.currentTimeMillis()
                                            );
            nodes.put(mac, nodeInfo);
        }
    }

    private static String MacToString(byte[] mac) {
        String res = "";
        for (int i = 0; i < mac.length; ++i) {
            res += hexArray[(mac[i] & 0xFF) / 16];
            res += hexArray[(mac[i] & 0xFF) % 16];
            if (i != mac.length - 1) {
                res += "::";
            }
        }
        return res;
    }
}