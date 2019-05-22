package com.kute.akka.quickstart;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.kute.akka.quickstart.caseclass.Message;
import io.vavr.control.Try;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * created by bailong001 on 2019/05/08 16:39
 */
public final class QuickApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuickApplication.class);

    private static ActorSystem system;
    private ActorRef producerActor;

    @BeforeClass
    public void before() {
        system = ActorSystem.create("QuickApplication");
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        producerActor = system.actorOf(ProducerActor.props(consumerActor), "producerActor");
    }

    @Test
    public void test() {

        LOGGER.info("ProducerActor begin send message");

        IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            producerActor.tell(new Message("message-" + i), ActorRef.noSender());
        });
    }

    @Test
    public void test1() {
        producerActor.tell(new Message("message"), ActorRef.noSender());
        // send stop consumer-actor
        producerActor.tell("stopSelf", producerActor);
    }

    @Test
    public void test2() {
        producerActor.tell(new RuntimeException("KuteException"), ActorRef.noSender());
        producerActor.tell(new Message("message"), ActorRef.noSender());
    }

    @AfterClass
    public void after() {
        Try.run(() -> TimeUnit.SECONDS.sleep(20));

//        system.terminate();
        TestKit.shutdownActorSystem(system);
        system = null;
    }

}
