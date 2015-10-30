package ru.ifmo.ctddev.mazin.net.lab1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Output implements Runnable {
    private static final int TIME_FOR_RESPONSE = Main.SLEEP_TIME + 100;

    public void run() {
        while (true) {
            try {
                System.out.println("Current instances map:");

                Map<String, NodeInfo> nodes = Info.INSTANCE.getNodes();
                List<String> macs = new ArrayList<>(nodes.keySet());
                Collections.sort(macs);

                for (String mac : macs) {
                    NodeInfo nodeInfo = nodes.get(mac);
                    if (System.currentTimeMillis() - nodeInfo.lastReceived > TIME_FOR_RESPONSE) {
                        nodeInfo.incMissedNumber();
                    }
                    if (nodeInfo.getMissedNumber() >= Info.MAX_MISSED_PACKETS) {
                        nodes.remove(mac);
                        continue;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("MAC: ").append(mac);
                    sb.append(" | Hostname: ").append(nodeInfo.hostname);
                    sb.append(" | Last UNIX Timestamp: ").append(nodeInfo.lastSent);
                    sb.append(" | Last time received: ").append(new SimpleDateFormat("HH:mm:ss").format(nodeInfo.lastReceived));
                    sb.append(" | Missed: ").append(nodeInfo.getMissedNumber());
                    System.out.println(sb);
                }

                System.out.print("\n#########################\n");

                Thread.sleep(Main.SLEEP_TIME / 2);
            } catch (InterruptedException e) {
                System.err.println(e);
                System.exit(1);
            }
        }
    }
}
