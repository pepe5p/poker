package com.pkaras;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;


class ClientTest {

    @BeforeAll
    static void beforeAllTests() {
        Client.clientChannel = mock(SocketChannel.class);
    }

    @DisplayName("Connecting to Server")
    @Test
    void testConnectToServer() throws IOException {
        SocketChannelProxy socketChannelProxy = mock(SocketChannelProxy.class);

        InetSocketAddress serverAddress = new InetSocketAddress(
            ConnectionConfig.serverAddress(),
            ConnectionConfig.PORT
        );
        Client.connectToServer(socketChannelProxy);
        verify(socketChannelProxy).open(serverAddress);
    }

    @DisplayName("Writing Message")
    @Test
    void testWriteLoop() throws IOException {
        BufferedReader bufferedReaderMock = mock(BufferedReader.class);
        when(bufferedReaderMock.readLine())
            .thenReturn("test")
            .thenReturn("test")
            .thenReturn("exit");
        Client.writeLoop(bufferedReaderMock);
        verify(Client.clientChannel, times(2)).write(ByteBuffer.wrap("test".getBytes()));
        verify(Client.clientChannel, times(0)).write(ByteBuffer.wrap("exit".getBytes()));
    }
}
