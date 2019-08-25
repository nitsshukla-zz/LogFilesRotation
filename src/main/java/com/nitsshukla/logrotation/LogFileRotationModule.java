package com.nitsshukla.logrotation;

import com.google.inject.AbstractModule;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;
import com.nitsshukla.logrotation.strategy.impl.CirculatLogFileRotationStrategyImpl;

public class LogFileRotationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LogFileRotationStrategy.class).to(CirculatLogFileRotationStrategyImpl.class);
    }
}
