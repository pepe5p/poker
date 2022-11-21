package com.pkaras;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketChannelProxy {
    public SocketChannel open(InetSocketAddress serverAddress) throws IOException {
        return SocketChannel.open(serverAddress);
    }
}
