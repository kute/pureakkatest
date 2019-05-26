package com.kute.akka.quickstart;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * created by bailong001 on 2019/05/11 11:04
 */
public class DoNothingActor extends AbstractLoggingActor {

    public static Props props(ActorRef actorRef) {
        return Props.create(DoNothingActor.class, DoNothingActor::new);
    }

    @Override
    public void preStart() throws Exception {
        log().info("preStart start at {}", getSelf());
    }

    @Override
    public void postStop() throws Exception {
        log().info("postStop start at {}", getSelf());
    }

    @Override
    public Receive createReceive() {
//        return receiveBuilder().build();
        return AbstractActor.emptyBehavior();
    }

    public static Props props() {
        return Props.create(DoNothingActor.class, DoNothingActor::new);
    }
}
