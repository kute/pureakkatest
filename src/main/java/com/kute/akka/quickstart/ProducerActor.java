package com.kute.akka.quickstart;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kute.akka.quickstart.caseclass.Message;
import com.kute.akka.util.TimeUtil;
import scala.Option;

/**
 * created by bailong001 on 2019/05/08 16:20
 */
public class ProducerActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef consumerActor;

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
        log.info("preStart start at {}", getSelf());
        TimeUtil.sleep(6);
        log.info("preStart end at {}", getSelf());
    }

    /**
     * 在 getContext().stop() 调用后 触发
     *
     * @throws Exception
     */
    @Override
    public void postStop() throws Exception {
        log.info("postStop at {}", getSelf());
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        log.info("preRestart at={}, reason={}, message={}", getSelf(), reason.getMessage(), message);
        TimeUtil.sleep(6);
        log.info("preRestart end at={}, reason={}, message={}", getSelf(), reason.getMessage(), message);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        log.info("postRestart at={}, reason={}", getSelf(), reason.getMessage());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, event -> {
                    log.info("ProducerActor Receive message={}, self={}, sender={}", event.getMessage(), getSelf(), getSender());
                    consumerActor.tell(event, getSelf());
                })
                .matchEquals("stopSelf", p -> {
                    getContext().stop(getSelf());
                })
                .match(RuntimeException.class, e -> {
                    // 异常后 默认的策略：停止并重启actor
                    log.error("throw exception", e);
                    throw e;
                })
                .build();
    }

    public static Props props(ActorRef actorRef) {
        return Props.create(ProducerActor.class, () -> new ProducerActor(actorRef));
    }
}