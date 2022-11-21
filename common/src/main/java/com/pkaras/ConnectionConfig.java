package com.pkaras;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionConfig {
    public static final int PORT = 10000;

    public static InetAddress serverAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    private ConnectionConfig() {}
}
