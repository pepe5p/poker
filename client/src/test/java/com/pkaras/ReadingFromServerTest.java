package com.pkaras;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ReadingFromServerTest {

    static ReadingFromServer readingFromServer = new ReadingFromServer(mock(SocketChannel.class));

    @DisplayName("Reading Loop")
    @Test
    void testReadLoop() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(ReadingFromServer.BUFFER_SIZE);
        when(readingFromServer.clientChannel.read(byteBuffer))
            .thenReturn(0)
            .thenReturn(-1);
        readingFromServer.readLoop(byteBuffer);
        assertTrue(readingFromServer.clientChannel.isOpen());
    }
}
