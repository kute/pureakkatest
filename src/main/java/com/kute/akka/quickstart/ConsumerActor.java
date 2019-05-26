package com.kute.akka.quickstart;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kute.akka.quickstart.caseclass.Message;
import com.kute.akka.util.TimeUtil;

/**
 * created by bailong001 on 2019/05/08 15:47
 */
public class ConsumerActor extends AbstractLoggingActor {

    @Override
    public void preStart() throws Exception {
        log().info("preStart at {}", getSelf());
        getContext().actorOf(DoNothingActor.props(), "doNothingActorChild");
    }


    @Override
    public void postStop() throws Exception {
        log().info("postStop at {}", getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, message -> {
                    if (message.getMessage().contains("6")) {
                        TimeUtil.sleep(2);
                    }
                    log().info("ConsumerActor receive message={}, self={}, sender={}",
                            message.getMessage(), getSelf(), getSender());
                })
                .matchEquals("stopSelf", p -> {
                    getContext().stop(getSelf());
                })
                .build();
    }

    public static Props props() {
        return Props.create(ConsumerActor.class, ConsumerActor::new);
    }
}
