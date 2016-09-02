package com.tiantian.strangergame.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 */
public class ClusterActorManager {
    private static ActorSystem actorSystem;
    private static ActorRef actorRef;
    private static String SHARDING_ACTOR_NAME = "StrangerClusterActorSystem";

    public static void init(String port) {
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port).withFallback(
                ConfigFactory.load("clusterconfig.conf"));

        actorSystem = ActorSystem.create(SHARDING_ACTOR_NAME, config);
        actorRef = actorSystem.actorOf(Props.create(ShardingManager.class, port), "ShardingManager");
        ClusterClientReceptionist.get(actorSystem).registerService(actorRef);
    }
}
