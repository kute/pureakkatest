package com.kute.akka.quickstart;

import akka.actor.AbstractActorWithTimers;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.time.Duration;

/**
 * created by bailong001 on 2019/09/14 10:10
 */
public class TimerActor extends AbstractActorWithTimers {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static final String TICK_KEY = "";

    public TimerActor() {
        getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofSeconds(5));
    }

    public static final class FirstTick {
    }

    public static final class Tick {
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(FirstTick.class, message -> {
                    log.info("收到发起心跳检查的指示，开始发起周期性心跳检查");
                    getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofSeconds(1));
                })
                .match(Tick.class, message -> {
                    log.info("接收到心跳检查。。。");
                })
                .match(ReceiveTimeout.class, p -> {
                    log.info("没有接受到任何消息，需要检查应用或者邮件通知");
                    getContext().setReceiveTimeout(scala.concurrent.duration.Duration.Undefined());
                })
                .build();
    }

    public static Props props() {
        return Props.create(TimerActor.class, TimerActor::new);
    }
}
