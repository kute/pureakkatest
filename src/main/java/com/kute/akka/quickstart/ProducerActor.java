package com.kute.akka.quickstart;

import akka.actor.*;
import com.kute.akka.quickstart.caseclass.ActorMessage;
import com.kute.akka.quickstart.caseclass.Message;
import com.kute.akka.util.TimeUtil;
import scala.Option;

import java.time.Duration;

/**
 * created by bailong001 on 2019/05/08 16:20
 * <p>
 * AbstractLoggingActor: log() == private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this)
 */
//public class ProducerActor extends AbstractActor {
public class ProducerActor extends AbstractLoggingActor {

    private ActorRef consumerActor;

    public ProducerActor() {
        // 设置该actor最多允许没有消息的超时时间，默认支持的最小粒度是 1ms，典型的应用是 心跳检查，在规定周期内没消息则触发该 超时消息
        // 一旦设置，则 在超时周期后会不断触发 超时消息，即 ReceiveTimeout 消息，若需要关闭此配置，则传递 Duration.Undefined()
        getContext().setReceiveTimeout(Duration.ofSeconds(1));
//        getContext().setReceiveTimeout(scala.concurrent.duration.Duration.Undefined()); // 关闭此 超时时间 触发
    }

    public ProducerActor(ActorRef consumerActor) {
        this.consumerActor = consumerActor;
    }

    /**
     * 在 actor 处理之前调用
     *
     * @throws Exception
     */
    @Override
    public void preStart() throws Exception {
        log().info("preStart start at {}", getSelf());
        TimeUtil.sleep(6);
        log().info("preStart end at {}", getSelf());
    }

    /**
     * 在 getContext().stop() 调用后 触发
     *
     * @throws Exception
     */
    @Override
    public void postStop() throws Exception {
        log().info("postStop at {}", getSelf());
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        log().info("preRestart at={}, reason={}, message={}", getSelf(), reason.getMessage(), message);
        TimeUtil.sleep(6);
        log().info("preRestart end at={}, reason={}, message={}", getSelf(), reason.getMessage(), message);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        log().info("postRestart at={}, reason={}", getSelf(), reason.getMessage());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // match 顺序也是有关系的
                .match(Message.class, event -> {
                    log().info("ProducerActor Receive message={}, self={}, sender={}", event.getMessage(), getSelf(), getSender());
                    consumerActor.tell(event, getSelf());
                })
                .match(RuntimeException.class, e -> {
                    // 异常后 默认的策略：停止并重启actor
                    log().error("throw exception", e);
                    throw e;
                })
                // 收到 要 监视子节点的消息
                .match(ActorMessage.class, actorMessage -> "toWatchChild".equals(actorMessage.getAction()), actorMessage -> {
                    log().info("Recevie watch message for child={}", actorMessage.getActorRef().path());
                    getContext().watch(actorMessage.getActorRef());
                })
                // 监听 所有监视的actor的终止消息
                .match(Terminated.class, terminated -> {
                    log().info("Receive actor={} terminated", terminated.actor().path());
                })
                .matchEquals("HeartBitCheck", p -> {
                    log().info("接收到心跳检查。。。");
                })
                // 超时消息被触发，即 在 超时时间周期内 没有任何消息 发送过来
                .match(ReceiveTimeout.class, receiveTimeout -> {
                    log().info("没有接受到任何消息，需要检查应用或者邮件通知");
                    // 关闭 此超时消息的 发送
//                    getContext().cancelReceiveTimeout(); // 不起作用这个？
                    getContext().setReceiveTimeout(scala.concurrent.duration.Duration.Undefined());
                })
                .matchAny(o -> {
                    log().info("未知消息：{}", o);
                })
                .build();
    }

    public static Props props(ActorRef actorRef) {
        if (null == actorRef) {
            return Props.create(ProducerActor.class, ProducerActor::new);
        }
        return Props.create(ProducerActor.class, () -> new ProducerActor(actorRef));
    }
}
