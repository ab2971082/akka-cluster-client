package com.tiantian.strangergame.server;


import com.tiantian.strangergame.akka.ClusterActorManager;
import com.tiantian.strangergame.data.mongodb.MGDatabase;
/**
 *
 */
public class StrangerGameServer {
    public static void main(String[] args) {
//        String defaultPort = "2550";
//        if (args != null) {
//            defaultPort = args[0];
//        }
//        ClusterActorManager.init(defaultPort);

        MGDatabase.getInstance().init();

        ClusterActorManager.init("2560");
        ClusterActorManager.init("2561");
    }
}
