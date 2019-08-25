package com.nitsshukla.logrotation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nitsshukla.logrotation.strategy.LogFileRotationStrategy;

public class LogFileRotationMain {
    static Injector injector;
    public static void main(String[] args) {
        injector = Guice.createInjector(new LogFileRotationModule());
        //LogFileRotationStrategy strategy = injector.getInstance(LogFileRotationStrategy.class);
        //strategy.log("12323");
    }
}
