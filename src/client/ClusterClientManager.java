package com.tiantian.stranger.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ClusterClientManager {
    private static ActorSystem actorSystem;
    private static ActorRef client;
    private static final String SEND_ACTOR_PATH = "/user/ShardingManager";

    public static void init (String port) {
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port).withFallback(
                ConfigFactory.load("clusterconfig.conf"));

        actorSystem = ActorSystem.create("StrangerActorSystem", config);
        client = actorSystem.actorOf(ClusterClient.props(
                        ClusterClientSettings.create(actorSystem)),
                "client");
    }

    private static void checkInit() {
        if (actorSystem == null) {
            throw new RuntimeException("actorSystem not init");
        }
        if (client == null) {
            throw new RuntimeException("client not init");
        }
    }

    public static void send(Object object) {

        checkInit();
        client.tell(new ClusterClient.Send(SEND_ACTOR_PATH,  object, true),
                ActorRef.noSender());
    }

    public static void sendToAll(Object object) {
        checkInit();
        client.tell(new ClusterClient.SendToAll(SEND_ACTOR_PATH,  object),
                ActorRef.noSender());
    }

    public static Object sendAndWait(Object object) {
        return sendAndWait(object, 30);
    }

    public static Object sendAndWait(Object object, long waitSec) {
        checkInit();
        Timeout timeout = Timeout.apply(Math.max(1, waitSec), TimeUnit.SECONDS);
        Future<Object> future = Patterns.ask(client,
                new ClusterClient.Send(SEND_ACTOR_PATH, object), timeout);
        Object result = null;
        try {
            result = Await.result(future, timeout.duration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
