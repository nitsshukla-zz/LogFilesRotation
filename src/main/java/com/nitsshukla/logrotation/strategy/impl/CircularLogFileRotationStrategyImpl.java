package com.nitsshukla.logrotation.strategy.impl;

import com.google.inject.assistedinject.Assisted;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import static java.nio.file.StandardOpenOption.*;

public class CircularLogFileRotationStrategyImpl implements LogFileRotationStrategy {

    private static final String FILE_NAME = "client.log";
    private static byte[] DELIMETER = "\n".getBytes(StandardCharsets.UTF_8);

    private final long maxSizeInBytes;
    private long sizeInBytes;
    private final MappedByteBuffer mappedByteBuffer;
    private File file = new File(FILE_NAME);

    public CircularLogFileRotationStrategyImpl(@Assisted long sizeInBytes) throws Exception {
        this.maxSizeInBytes = sizeInBytes;
        //initiate mappedbytebuffer
        Path path = getFileURIFromResources(FILE_NAME);
        FileChannel channel = (FileChannel) Files.newByteChannel(path, EnumSet.of(READ, WRITE, CREATE));
        mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, maxSizeInBytes);
        sizeInBytes = mappedByteBuffer.position();
    }

    private Path getFileURIFromResources(String fileName) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }
        return Paths.get(file.getAbsolutePath());
    }

    @Override
    public void log(String logMessage) {
        //Simple case
        //First 4 bytes tell me the position i need to start from.
        //Check if size of file exceeds maxSizeInBytes or flag of reversal is ON
        //If no - Then just append //
        //If yes, come to the seek and remove log entries till it makes space for current entry.
        //System.out.println(logMessage);
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(logMessage);
        if (sizeInBytes + buffer.limit() + DELIMETER.length > maxSizeInBytes) {
            return;
        }
        sizeInBytes += buffer.position();
        mappedByteBuffer.put(buffer);
        mappedByteBuffer.put(DELIMETER);
        sizeInBytes += buffer.limit() + DELIMETER.length;
    }

    @Override
    public ByteBuffer getAllLogs() {
        //return mappedByteBuffer.get(new byte[mappedByteBuffer.position()]);
        mappedByteBuffer.flip();
        return mappedByteBuffer.slice();
    }
}
