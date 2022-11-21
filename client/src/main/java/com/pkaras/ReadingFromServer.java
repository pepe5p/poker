package com.pkaras;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class ReadingFromServer implements Runnable {
    final SocketChannel clientChannel;
    static final int BUFFER_SIZE = 1024;

    ReadingFromServer(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void run() {
        try {
            this.readLoop(ByteBuffer.allocate(BUFFER_SIZE));
            this.closeConnection();
        }
        catch (IOException e) {
            Console.print((e.getMessage()));
        }
    }

    void readLoop(ByteBuffer serverResponse) throws IOException {
        int serverIntResponse;
        while (true) {
            try {
                serverIntResponse = clientChannel.read(serverResponse);
            }
            catch (SocketException e) { return; }
            if (serverIntResponse == -1) return;
            serverResponse.flip();
            String data = new String(serverResponse.array()).trim();
            Console.print(data);
            serverResponse.clear();
            serverResponse.put(new byte[BUFFER_SIZE]);
            serverResponse.clear();
        }
    }

    private void closeConnection() throws IOException {
        Console.print("disconnected from server");
        clientChannel.close();
    }
}
