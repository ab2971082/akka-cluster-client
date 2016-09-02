package com.tiantian.stranger.server;

import com.tiantian.stranger.akka.ClusterClientManager;
import com.tiantian.stranger.akka.user.TableUserStatusEvent;
import com.tiantian.stranger.data.mongodb.MGDatabase;
import com.tiantian.stranger.data.postgresql.PGDatabase;
import com.tiantian.stranger.handler.StrangerEventHandler;
import com.tiantian.stranger.settings.PGConfig;
import com.tiantian.stranger.settings.StrangerEventConfig;
import com.tiantian.stranger.thrift.event.StrangerEventService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class StrangerEventServer {
    private static Logger LOG = LoggerFactory.getLogger(StrangerEventServer.class);
    private TThreadPoolServer server;
    private TThreadPoolServer.Args sArgs;


    public static void main(String[] args) {
        ClusterClientManager.init((StrangerEventConfig.getInstance().getPort() + 5) + "");
        //TODO 测试    
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 5; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3000; i++) {
                                ClusterClientManager.send(new TableUserStatusEvent(i + ""));
                            }
                        }
                    }).start();
                }

            }
        }).start();

        StrangerEventServer strangerEventServer = new StrangerEventServer();
        strangerEventServer.init(args);
        strangerEventServer.start();
    }

    public void init(String[] arguments) {
      
    }

    public void start() {
        LOG.info("start");
        server = new TThreadPoolServer(sArgs);
        LOG.info("the server listen in {}", StrangerEventConfig.getInstance().getPort());
        server.serve();
    }

    public void stop() {
        LOG.info("stop");
        server.stop();
    }

    public void destroy() {
        LOG.info("destroy");
    }
}
