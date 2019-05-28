package com.kute.akka.quickstart.caseclass;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * created by bailong001 on 2019/05/28 18:50
 */
@Getter
@AllArgsConstructor
public class ActorMessage {
    private final String action;
    private final ActorRef actorRef;
}
