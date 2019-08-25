package com.nitsshukla.logrotation;

import com.google.inject.AbstractModule;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;
import com.nitsshukla.logrotation.strategy.impl.CircularLogFileRotationStrategyImpl;

public class LogFileRotationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LogFileRotationStrategyFactory.class).to(CircularFileRotationStrategyFactory.class);
    }

    public interface LogFileRotationStrategyFactory {
        LogFileRotationStrategy createStrategy(long size);
    }
    public static class CircularFileRotationStrategyFactory implements LogFileRotationStrategyFactory {

        @Override
        public LogFileRotationStrategy createStrategy(long size) {
            return new CircularLogFileRotationStrategyImpl(size);
        }
    }
}
