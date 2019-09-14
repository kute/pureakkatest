package com.kute.akka.quickstart;

import akka.actor.*;
import akka.testkit.javadsl.TestKit;
import com.google.common.truth.Truth;
import com.kute.akka.quickstart.caseclass.Message;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Flux;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * created by bailong001 on 2019/05/08 16:39
 */
@Slf4j
public final class QuickApplication {

    private static ActorSystem system;
    private ActorRef producerActor;

    @BeforeClass
    public static void beforeStatic() {

    }

    @Before
    public void before() {
        system = ActorSystem.create("QuickApplication");

        // 死信：DeadLetterActorRef
        system.deadLetters();
    }

    /**
     * 测试 消息异步发送
     */
    @Test
    public void testAsync() {
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
//        system.actorFor(); // actorFor已废弃
        log.info("testSync send message begin");
        consumerActor.tell(new Message("message-6"), ActorRef.noSender());
        log.info("testSync send message stop");
    }

    @Test
    public void test() {

        log.info("ProducerActor begin send message");
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
    public void test1() throws InterruptedException, ExecutionException, TimeoutException {
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        consumerActor.tell("stopSelf", consumerActor);

        // 其他方式
//        consumerActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
//        consumerActor.tell(Kill.getInstance(), ActorRef.noSender());

        // 优雅停止
//        CompletionStage<Boolean> stage = Patterns.gracefulStop(consumerActor, Duration.ofSeconds(5), "shutdown");
//        stage.toCompletableFuture().get(6, TimeUnit.SECONDS);
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
        }).onFailure(ex -> log.error("", ex));
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
        }).onFailure(ex -> log.error("", ex));

    }

    @Test
    public void testWatch() {
        ActorRef consumerActor = system.actorOf(ConsumerActor.props(), "consumerActor");
        producerActor = system.actorOf(ProducerActor.props(consumerActor), "producerActor");
        consumerActor.tell("stopChild", producerActor);
    }

    /**
     * 通过 ActorSelection 获取 actorRef
     */
    @Test
    public void testSelection() {
        system.actorOf(ConsumerActor.props(), "consumerActor");
        ActorSelection actorSelection = system.actorSelection("/user/QuickApplication/consumerActor");
        // 方法一:resolveOne
        actorSelection.resolveOne(Duration.ofSeconds(1)).whenComplete((actorRef, throwable) -> {
            System.out.println(actorRef.path().toString());
        });
        // 方式二: 通过 给 actorSelection发消息，然后再消息处理中通过 getSelf获取
//        actorSelection.tell("", ActorRef.noSender());
    }

    /**
     * 心跳检查
     */
    @Test
    public void testReceiveTimeout() throws InterruptedException {
        producerActor = system.actorOf(ProducerActor.props(null), "producerActor");

        Flux.interval(Duration.ofSeconds(1))
                .doOnEach(signal -> {
                    Long value = signal.get();
                    if (null != value && value < 10) {
                        producerActor.tell("HeartBitCheck", ActorRef.noSender());
                    }
                })
                .subscribe();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

    /**
     * 心跳检查 版本 2
     */
    @Test
    public void testHeartBitCheck() {
        system.actorOf(TimerActor.props(), "timerActor");
    }

    @After
    public void after() {
        Try.run(() -> TimeUnit.SECONDS.sleep(20));

//        system.terminate();
        TestKit.shutdownActorSystem(system);
        system = null;
    }

}
