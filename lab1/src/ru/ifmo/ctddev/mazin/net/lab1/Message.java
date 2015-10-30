package ru.ifmo.ctddev.mazin.net.lab1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Message {
    private static final int TIMESTAMP_LENGTH = Long.SIZE / Byte.SIZE; // 64 / 8 -> 8 bytes
    private static final int MAC_LENGTH = 6;
    private static final int HOSTNAME_MAX_LENGTH = 256;

    private byte[] mac;
    private byte hostnameLength;
    private byte[] hostname; // in UTF-8
    private byte[] unixTimeStamp;

    public Message(byte[] mac, String hostname) {
        assert mac != null &&
               hostname != null &&
               mac.length == MAC_LENGTH &&
               hostname.length() < HOSTNAME_MAX_LENGTH;

        this.mac = Arrays.copyOfRange(mac, 0, mac.length);
        this.hostname = hostname.getBytes(Charset.forName("UTF-8"));
        hostnameLength = (byte) hostname.length();

        long currentTimeSeconds = System.currentTimeMillis() / 1000L;
        unixTimeStamp = ByteBuffer.allocate(TIMESTAMP_LENGTH)
                                  .putLong(currentTimeSeconds)
                                  .array();
    }

    public Message(byte[] data) throws BadMessageException {
        if (data.length < MAC_LENGTH + 1 + TIMESTAMP_LENGTH) {
            throw new BadMessageException("Received data is to small");
        }
        mac = Arrays.copyOfRange(data, 0, MAC_LENGTH);
        hostnameLength = data[MAC_LENGTH];
        if (data.length < MAC_LENGTH + 1 + hostnameLength + TIMESTAMP_LENGTH) {
            throw new BadMessageException("Received data does not contain proper hostname and UNIS timestamp");
        }
        hostname = Arrays.copyOfRange(data,
                                      MAC_LENGTH + 1,
                                      MAC_LENGTH + 1 + hostnameLength);
        unixTimeStamp = Arrays.copyOfRange(data,
                                           MAC_LENGTH + 1 + hostnameLength,
                                           MAC_LENGTH + 1 + hostnameLength + TIMESTAMP_LENGTH);
    }

    public byte[] getData() {
        byte[] data = new byte[mac.length + 1 + hostname.length + unixTimeStamp.length];
        System.arraycopy(mac, 0, data, 0, mac.length);
        data[mac.length] = hostnameLength;
        System.arraycopy(hostname, 0, data, mac.length + 1, hostname.length);
        System.arraycopy(unixTimeStamp, 0, data, mac.length + 1 + hostname.length, unixTimeStamp.length);

        return data;
    }

    public byte[] getMac() {
        return Arrays.copyOfRange(mac, 0, mac.length);
    }

    public String getHostname() {
        return new String(hostname, Charset.forName("UTF-8"));
    }

    public long getUnixTimestamp(){
        return ByteBuffer.wrap(unixTimeStamp)
                         .order(ByteOrder.BIG_ENDIAN)
                         .getLong();
    }
}
