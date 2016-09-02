package com.tiantian.strangergame.akka;

import akka.actor.*;
import com.tiantian.stranger.akka.event.Event;
import com.tiantian.stranger.akka.event.TableEvent;
import com.tiantian.strangergame.akka.actor.TableActor;
import com.tiantian.strangergame.akka.event.GameOver;
import org.apache.commons.lang.StringUtils;
import scala.concurrent.duration.Duration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class TableManagerActor extends UntypedActor {
    private Map<String, ActorRef> tableActorMap = new ConcurrentHashMap<>();
//    private static final AtomicBoolean FLAG = new AtomicBoolean(true);

    public void preStart() {
//        new Thread(() -> {
//            while (FLAG.get()) {
//                try {
//                    TimeUnit.SECONDS.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    break;
//                }
//                Collection<ActorRef> allActors = tableActorMap.values();
//                for (ActorRef actorRef : allActors) {
//                     if (actorRef != null) {
//                         actorRef.tell(message, getSender());
//                     }
//                }
//            }
//        }).start();
    }

    public void postStop() {
//        FLAG.set(false);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Event) {
            if (message instanceof TableEvent) {
                String tableId = ((TableEvent) message).tableId();
                ActorRef tableActor = tableActorMap.get(tableId);
                if (tableActor == null) {
                    tableActor = getContext().actorOf(Props.create(TableActor.class, tableId)
                            .withDispatcher("dbDispatcher"), "Table_" + tableId);
                    tableActorMap.put(tableId, tableActor);
                }
                tableActor.tell(message, getSender());
            }
        }
        else if (message instanceof GameOver) { // 关闭桌子
            String tableId = ((GameOver) message).tableId();
            if (StringUtils.isNotBlank(tableId)) {
                ActorRef tableActor = tableActorMap.remove(tableId);
                if (tableActor != null) {
                    tableActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
                }
            }
        }
        else {
            unhandled(message);
        }
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(
                    1, Duration.create("1 minute"),
                    t -> {
                        return SupervisorStrategy.resume();
                    });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

}
