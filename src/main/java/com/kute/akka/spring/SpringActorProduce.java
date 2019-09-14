package com.kute.akka.spring;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * created by bailong001 on 2019/05/24 10:25
 * IndirectActorProducer 定制化actor的创建策略，如 允许 支持依赖注入的框架 决定实际的actor class是什么
 * 当使用依赖注入时，actor bean 不允许是单例
 */
public class SpringActorProduce implements IndirectActorProducer {

    private ApplicationContext applicationContext;
    private final String beanName;
    private final Object[] args;

    public SpringActorProduce(ApplicationContext applicationContext, String beanName, Object[] args) {
        this.applicationContext = applicationContext;
        this.beanName = beanName;
        this.args = args;
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(beanName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(beanName);
    }
}
