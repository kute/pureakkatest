package com.kute.akka.quickstart;

import akka.actor.*;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.google.common.truth.Truth;
import com.kute.akka.quickstart.caseclass.Message;
import com.kute.akka.quickstart.caseclass.StopMessage;
import com.kute.akka.util.TimeUtil;
import io.vavr.control.Try;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
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
    public static void beforeStatic() {

    }

    @Before
    public void before() {
        system = ActorSystem.create("QuickApplication");
    }

    @Test
    public void test() {

        LOGGER.info("ProducerActor begin send message");
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        producerActor = system.actorOf(ProducerActor.props(consumerActor), "producerActor");

        IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            producerActor.tell(new Message("message-" + i), ActorRef.noSender());
        });
    }

    /**
     * 测试 当 停止 自己时，子节点先都停止
     */
    @Test
    public void test1() {
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        consumerActor.tell("stopSelf", consumerActor);
    }

    /**
     * 测试 当 出现未捕获异常时，actor默认会重启
     */
    @Test
    public void test2() {
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        producerActor = system.actorOf(ProducerActor.props(consumerActor), "producerActor");
        producerActor.tell(new RuntimeException("KuteException"), ActorRef.noSender());
        producerActor.tell(new Message("message"), ActorRef.noSender());
    }

    /**
     * Inbox 信箱，具有 收/发 消息的功能，还有 监视actor的功能
     */
    @Test
    public void testInbox() {

        ActorRef targetActor = system.actorOf(ConsumerActor.props(), "targetActor");

        final Inbox inbox = Inbox.create(system);

        String msg = "map";

        // 发送消息，send方法是对tell方法的封装
        inbox.send(targetActor, msg);

        Try.run(() -> {
            // 接收消息
            Object message = inbox.receive(Duration.ofSeconds(10));
            Truth.assertThat(message).isNotNull();
            Truth.assertThat(message).isEqualTo(msg);
        }).onFailure(ex -> LOGGER.error("", ex));
    }

    /**
     * 测试 监视
     */
    @Test
    public void testInbox2() {

        ActorRef targetActor = system.actorOf(ConsumerActor.props(), "targetActor");

        final Inbox inbox = Inbox.create(system);

        inbox.watch(targetActor);

        String msg = "map";

        targetActor.tell(msg, ActorRef.noSender());

        Try.run(() -> {
            Terminated terminated = (Terminated) inbox.receive(FiniteDuration.create(1L, TimeUnit.SECONDS));
            System.out.println(terminated.toString());
            System.out.println(terminated.getActor().path());
        }).onFailure(ex -> LOGGER.error("", ex));

    }

    @Test
    public void testWatch() {
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        producerActor = system.actorOf(ProducerActor.props(consumerActor), "producerActor");
        consumerActor.tell("stopChild", producerActor);
    }


    @After
    public void after() {
        Try.run(() -> TimeUnit.SECONDS.sleep(20));

//        system.terminate();
        TestKit.shutdownActorSystem(system);
        system = null;
    }

}
