package com.tiantian.strangergame.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion;
import com.tiantian.stranger.akka.event.Event;
import com.tiantian.stranger.akka.event.TableEvent;
import com.tiantian.stranger.akka.user.TableUserStatusEvent;
import com.tiantian.strangergame.akka.event.Game;

/**
 *
 */
public class ShardingManager extends UntypedActor {
    private String port;
    private ActorRef shardRegion;
    private Cluster cluster;

    public ShardingManager(String port) {
        this.port = port;
    }

    public void preStart () throws Exception {
        super.preStart();
        cluster = Cluster.get(getContext().system());
        cluster.subscribe(self(), ClusterEvent.MemberEvent.class);
        ClusterShardingSettings settings = ClusterShardingSettings.create(getContext().system());
        this.shardRegion = ClusterSharding.get(getContext().system()).start("ClusterTableManagerActor",
                Props.create(TableManagerActor.class), settings, messageExtractor);
    }
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Event || message instanceof Game) {
            String tableId = ((TableUserStatusEvent) message).tableId();
            if (tableId.equalsIgnoreCase("0") || tableId.equalsIgnoreCase("2999")) {
                System.out.println(System.currentTimeMillis() + ":" + port + ":" + tableId);
            }
             shardRegion.forward(message, getContext());
         }
         else {
            unhandled(message);
         }
    }


    private static ShardRegion.HashCodeMessageExtractor messageExtractor = new ShardRegion.HashCodeMessageExtractor(20) {
        @Override
        public String entityId(Object o) {
             if(o instanceof TableEvent) {
                return ((TableEvent) o).tableId();
             } else if (o instanceof Game) {
                 return ((Game) o).tableId();
             }
             return null;
        }
    };
}
