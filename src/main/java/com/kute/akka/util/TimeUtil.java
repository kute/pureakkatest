package com.kute.akka.util;

import io.vavr.control.Try;

import java.util.concurrent.TimeUnit;

/**
 * created by bailong001 on 2019/05/08 20:35
 */
public final class TimeUtil {

    public static void sleep(final int seconds) {
        Try.run(() -> TimeUnit.SECONDS.sleep(seconds));
    }

    public static void sleepForever() {
        Try.run(() -> {
            while (true) {

            }
        });
    }

}
