package com.pkaras;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client
{
    static SocketChannel clientChannel;

    public static void main(String[] args) {
        try {
            SocketChannelProxy socketChannelProxy = new SocketChannelProxy();
            Client.connectToServer(socketChannelProxy);
            Client.delayReadLoopInThread();
            Client.writeLoop(new BufferedReader(new InputStreamReader(System.in)));
            Client.closeConnection();
        }
        catch (IOException e) {
            Console.print(e.getMessage());
        }
    }

    static void connectToServer(SocketChannelProxy socketChannelProxy) throws IOException {
        Client.clientChannel = socketChannelProxy.open(
            new InetSocketAddress(
                ConnectionConfig.serverAddress(),
                ConnectionConfig.PORT
            )
        );
    }

    private static void delayReadLoopInThread() {
        new Thread(
            new ReadingFromServer(clientChannel)
        ).start();
    }

    static void writeLoop(BufferedReader keyboardReader) throws IOException {
        while (true) {
            String inputString = keyboardReader.readLine();
            ByteBuffer myBuffer = ByteBuffer.wrap(inputString.getBytes());
            if (inputString.equals("exit")) break;
            clientChannel.write(myBuffer);
        }
    }

    private static void closeConnection() throws IOException {
        Console.print("\u001B[32mclosing connection\u001B[0m");
        clientChannel.close();
    }
}