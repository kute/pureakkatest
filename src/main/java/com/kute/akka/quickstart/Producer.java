package com.kute.akka.quickstart;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kute.akka.quickstart.caseclass.Message;

/**
 * created by bailong001 on 2019/05/08 16:20
 */
public class Producer extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef consumerActor;

    public Producer(ActorRef consumerActor) {
        this.consumerActor = consumerActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, event -> {
                    log.info("Producer Receive message={}, self={}, sender={}", event.getMessage(), getSelf(), getSender());

                    consumerActor.tell(event, getSelf());
                }).build();
    }

    public static Props props(ActorRef actorRef) {
        return Props.create(Producer.class, () -> new Producer(actorRef));
    }
}
