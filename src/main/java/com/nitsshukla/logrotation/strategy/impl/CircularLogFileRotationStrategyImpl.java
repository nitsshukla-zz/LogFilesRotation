package com.nitsshukla.logrotation.strategy.impl;

import com.google.inject.assistedinject.Assisted;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;

import java.nio.ByteBuffer;

public class CircularLogFileRotationStrategyImpl implements LogFileRotationStrategy {

    private final long maxSizeInBytes;

    public CircularLogFileRotationStrategyImpl(@Assisted long sizeInBytes) {
        this.maxSizeInBytes = sizeInBytes;
        //initiate mappedbytebuffer
    }

    @Override
    public void log(String logMessage) {
        //Simple case
        //Check if size of file exceeds maxSizeInBytes
        //If no - Then just append
        //If yes, come to the top and remove log entries till it makes space for current entry.
        System.out.println(logMessage);
    }

    @Override
    public ByteBuffer getAllLogs() {
        return null;
    }
}