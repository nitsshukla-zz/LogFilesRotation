package com.nitsshukla.logrotation.strategy.impl;

import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;

import java.nio.ByteBuffer;

public class CircularLogFileRotationStrategyImpl implements LogFileRotationStrategy {

    @Override
    public void log(String logMessage) {
        System.out.println(logMessage);
    }

    @Override
    public ByteBuffer getAllLogs() {
        return null;
    }
}
