package com.kute.akka.quickstart.caseclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * created by bailong001 on 2019/05/28 18:13
 */
@Getter
@AllArgsConstructor
public final class StopMessage {
    private final String message;
}
