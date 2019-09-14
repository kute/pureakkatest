#### wiki
- https://developer.lightbend.com/guides/akka-quickstart-java/
- https://akka.io/docs/
- https://github.com/write2munish/Akka-Essentials
- https://github.com/typesafehub/activator-akka-java-spring
- https://blog.csdn.net/liubenlong007/article/details/53782966

#### tip
- parent actor stop, so all child actors stop too
- actor 最好不要有阻塞太长的逻辑
- actor 之间传递的消息最好是 不可变的
- actor 是 行为和状态的 封装
- actor 即使没有引用也不会自动stop，比较简单的做法是 stop parent会自动stop all children

#### 终止actor方式
- getContext().stop(child)
- actor.tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
- actor.tell(akka.actor.Kill.getInstance(), ActorRef.noSender());

#### class
- AbstractLoggingActor: 带有 log() 的 AbstractActor


#### actor生命周期
- restart 只是交换实例对象，但是本身uid是不变的

![avatar](/files/actor_life_cycle.png)
![avatar](/files/actor_life_cycle_official.png)
actor hierarchy
![avatar](/files/actor_top_tree.png)

