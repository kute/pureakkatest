package com.kute.akka.spring;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * created by bailong001 on 2019/05/08 16:57
 */
@Component
public final class SpringPropsUtil implements Extension, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Props props(String beanName, Object... args) {
        // SpringActorProduce 构造函数
        return Props.create(SpringActorProduce.class, applicationContext, beanName, args);
    }
}
