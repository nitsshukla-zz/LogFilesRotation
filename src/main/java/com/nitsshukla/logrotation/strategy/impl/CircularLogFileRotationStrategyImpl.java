package com.nitsshukla.logrotation.strategy.impl;

import com.google.inject.assistedinject.Assisted;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import static java.lang.Integer.max;
import static java.nio.file.StandardOpenOption.*;

public class CircularLogFileRotationStrategyImpl implements LogFileRotationStrategy {

    private static final String FILE_NAME = "client.log";
    public static final byte DELIMETER = '\n';
    private static int DELIMETER_LENGTH = 1;

    private final int maxSizeInBytes;
    private int sizeInBytes;
    private boolean overflowed = false;
    private final MappedByteBuffer mappedByteBuffer;
    private File file = new File(FILE_NAME);

    public CircularLogFileRotationStrategyImpl(@Assisted long sizeInBytes) throws Exception {
        this.maxSizeInBytes = (int) sizeInBytes;
        //initiate mappedbytebuffer
        Path path = getFileURIFromResources();
        FileChannel channel = (FileChannel) Files.newByteChannel(path, EnumSet.of(READ, WRITE, CREATE));
        mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, maxSizeInBytes);
        this.sizeInBytes = max(8, mappedByteBuffer.position());
        mappedByteBuffer.position(0);
        mappedByteBuffer.putLong(this.sizeInBytes); //first 8 bytes for position
        mappedByteBuffer.position(this.sizeInBytes); //regain position
    }

    private Path getFileURIFromResources() throws Exception {
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
        int newSpaceNeeded = buffer.limit() + DELIMETER_LENGTH;
        if (!overflowed && sizeInBytes + newSpaceNeeded > maxSizeInBytes) {
            overflowed = true;
            mappedByteBuffer.position(8);
            sizeInBytes = 8;
        }
        if (overflowed) {
            //remove space needed
            makeSpaceForNewEntry(newSpaceNeeded);
        }
        sizeInBytes += buffer.position();
        mappedByteBuffer.put(buffer).put(DELIMETER);
        sizeInBytes += buffer.limit() + DELIMETER_LENGTH;
        int localPos = mappedByteBuffer.position();
        mappedByteBuffer.position(0)
                .putLong(sizeInBytes)
                .position(localPos);
        System.out.println(sizeInBytes);
    }

    private void makeSpaceForNewEntry(int newSpaceNeeded) {
        mappedByteBuffer.mark();
        int index = 0;

        while (index++ < newSpaceNeeded && mappedByteBuffer.getChar() != DELIMETER) {
            mappedByteBuffer.position(mappedByteBuffer.position()-2);
            mappedByteBuffer.put(DELIMETER);
            //mappedByteBuffer.position(mappedByteBuffer.position() + 1);
            System.out.println(mappedByteBuffer.position());
        }
        mappedByteBuffer.getChar();
        mappedByteBuffer.reset();
    }

    @Override
    public ByteBuffer getAllLogs() {
        //return mappedByteBuffer.get(new byte[mappedByteBuffer.position()]);
        /*if (!overflowed) {
            mappedByteBuffer.flip();
        } else {
            mappedByteBuffer.position(mappedByteBuffer.limit());
            mappedByteBuffer.flip();
        }
        mappedByteBuffer.position(8);*/

        //seek - limit-x
        //8-seek
        if (overflowed) {
            int x = getLastTrail();
            StringBuilder pinnadi = new StringBuilder();
            mappedByteBuffer.position(sizeInBytes);
            ByteBuffer buffer = ByteBuffer.allocate(1+mappedByteBuffer.capacity());
            buffer.put(mappedByteBuffer.get(new byte[mappedByteBuffer.capacity()-x-sizeInBytes]).flip().slice());
            mappedByteBuffer.position(8);
            //buffer.put(mappedByteBuffer.get(new byte[sizeInBytes-9]).flip().slice());
            while (true) { //this is messy, get rid off it
                byte c1 = mappedByteBuffer.get();
                if (c1==(byte)0 || mappedByteBuffer.position()>sizeInBytes)break;
                buffer.put(c1);
            }
            buffer.flip();
            return buffer.slice();
        } else {
            mappedByteBuffer.position(sizeInBytes);
            ByteBuffer byteBuffer = mappedByteBuffer.get(new byte[sizeInBytes-8], 0, sizeInBytes-8);
            byteBuffer.flip();
            return byteBuffer;
        }
    }

    private void print(ByteBuffer buf) {
        int pos = buf.position();
        buf.position(0);
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buf);
        System.out.println(pos+"----------------------");
        System.out.println(new String(charBuffer.array()));
        System.out.println("----------------------");
        buf.position(pos);
    }

    private int getLastTrail() {
        mappedByteBuffer.mark();
        int x = 0;
        while (mappedByteBuffer.get(mappedByteBuffer.limit()-x-1) == (byte)00) {
            x++;
            System.out.println("del: " + x);
        }
        mappedByteBuffer.reset();
        return x;
    }
}
