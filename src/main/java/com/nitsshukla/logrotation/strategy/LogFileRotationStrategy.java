package com.nitsshukla.logrotation.strategy;

import java.nio.ByteBuffer;

public interface LogFileRotationStrategy {
	void log(String logMessage);
    ByteBuffer getAllLogs();
}
