package com.kute.akka.quickstart;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.kute.akka.quickstart.caseclass.Message;
import com.sun.xml.internal.ws.util.CompletedFuture;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * created by bailong001 on 2019/05/08 16:39
 */
public final class QuickApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuickApplication.class);

    private ActorSystem system;

    @Before
    public void before() {
        system = ActorSystem.create("QuickApplication");
    }

    @Test
    public void test() {

        final ActorRef consumerActor = system.actorOf(Consumer.props(), "consumerActor");

        final ActorRef producerActor = system.actorOf(Producer.props(consumerActor), "producerActor");

        LOGGER.info("Producer begin send message");

        IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            producerActor.tell(new Message("message-" + i), ActorRef.noSender());
        });

        Try.run(() -> TimeUnit.SECONDS.sleep(2));

    }

    @Test
    public void after() {
        system.terminate();
    }

}
