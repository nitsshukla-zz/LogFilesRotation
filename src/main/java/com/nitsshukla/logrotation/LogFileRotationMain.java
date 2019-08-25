package com.nitsshukla.logrotation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;

public class LogFileRotationMain {
    static Injector injector;
    public static void main(String[] args) throws Exception {
        injector = Guice.createInjector(new LogFileRotationModule());
        LogFileRotationStrategy strategy =
                injector.getInstance(LogFileRotationModule.LogFileRotationStrategyFactory.class)
                .createStrategy(1234);
        strategy.log("12323");
    }
}
