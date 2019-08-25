package com.nitsshukla.logrotation;

import com.google.inject.AbstractModule;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;
import com.nitsshukla.logrotation.strategy.impl.CircularLogFileRotationStrategyImpl;

public class LogFileRotationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LogFileRotationStrategy.class).to(CircularLogFileRotationStrategyImpl.class);
    }
}
