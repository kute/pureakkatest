package com.kute.akka.quickstart;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import com.kute.akka.quickstart.caseclass.ActorMessage;
import com.kute.akka.quickstart.caseclass.Message;
import com.kute.akka.util.TimeUtil;

/**
 * created by bailong001 on 2019/05/08 15:47
 */
public class ConsumerActor extends AbstractLoggingActor {

    private ActorRef child;

    @Override
    public void preStart() throws Exception {
        log().info("preStart at {}", getSelf());
        child = getContext().actorOf(DoNothingActor.props(), "doNothingActorChild");
        // 监视 子节点 生命周期
        getContext().watch(child);
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
                .matchEquals("map", message -> {
                    log().info("Receive message={}", message);
                })
                .matchEquals("stopSelf", p -> {
                    getContext().stop(getSelf());
                })
                .matchEquals("stopChild", stopMessage -> {
                    log().info("actor={} begin stop child={}, hasCode={}",
                            getSelf().path(), child.path(), child.hashCode());
                    // 告诉 procuder监视consumer的child
                    getSender().tell(new ActorMessage("toWatchChild", child), getSelf());
                    // 终止子节点
//                    getContext().unwatch(child);
                    getContext().stop(child);
                })
                // 监听 特定子节点的 终止消息，注意：Terminated msg 对于watch 操作是独立的，即使 actor在 watch的同时已经停止 也会 收到 Terminated 消息，相应的 即使 Terminated 消息已经在信箱了，那么unwatch后 也不会处理这个消息
                .match(Terminated.class, terminated -> terminated.getActor().equals(child), terminated -> {
                    log().info("Child={} has terminated", terminated.actor().path());
                })
                .build();
    }

    public static Props props() {
        return Props.create(ConsumerActor.class, ConsumerActor::new);
    }
}
