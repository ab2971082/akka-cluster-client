package com.tiantian.stranger.akka.event;


import java.io.Serializable;

/**
 *
 */
public interface Event extends Serializable {
    String event();
}
