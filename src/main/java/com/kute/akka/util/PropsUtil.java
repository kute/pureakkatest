package com.kute.akka.util;

import akka.actor.Props;
import akka.japi.Creator;
import com.google.common.base.Preconditions;
import org.joor.Reflect;

/**
 * created by bailong001 on 2019/05/08 16:57
 */
public final class PropsUtil {

    public static <T> Props props(Class<T> tClass) {
        Preconditions.checkNotNull(tClass);
        T t = Reflect.on(tClass).create().get();
        return Props.create(tClass, (Creator<T>) () -> t);
    }

}
