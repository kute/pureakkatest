package com.kute.akka.quickstart;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * created by bailong001 on 2019/05/11 11:04
 */
public class DoNothingActor extends AbstractActor {

    public static Props props(ActorRef actorRef) {
        return Props.create(DoNothingActor.class, DoNothingActor::new);
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
